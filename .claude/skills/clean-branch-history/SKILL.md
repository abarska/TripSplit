---
name: clean-branch-history
description: Rewrite a feature branch's messy commit history (WIP, "fix typo", review fixups, reverts, merge-from-main) into a short, reviewable sequence of logical commits with conventional commit messages, after the user approves a proposed plan. Use this whenever the user wants to clean up, tidy, squash, reorganize, reword or "make presentable" the commits on a branch, prepare a branch for a PR or review, or mentions interactive rebase, fixup/squash commits, or conventional commit messages for existing commits — even if they don't say "rebase".
---

# Clean branch history

Turn the commits on the current branch into a short, logical story that a reviewer can read commit by commit, without changing the final code. The final tree must be byte-identical to what the user started with. Only the way the changes are grouped and described is different.

Rewriting history is destructive and hard to undo for someone who doesn't know about reflog, so the workflow is: **analyze → propose → get explicit approval → back up → execute → verify → present**. Never skip the approval step, even if the plan looks obvious.

Interactive editors don't work in this environment (`git rebase -i` would hang waiting for an editor). Every rebase here is driven non-interactively by writing the todo list to a file and handing it to git through `GIT_SEQUENCE_EDITOR`. Phase 5 has the details.

## Phase 1: Pre-flight checks

Run these before analyzing anything. If one fails, stop and tell the user instead of working around it:

1. **No rebase in progress.** If either path printed by `git rev-parse --git-path rebase-merge --git-path rebase-apply` exists, stop. (Don't check `.git/` directly: in a worktree `.git` is a file.)
2. **Clean working tree.** `git status --porcelain` must be empty. If it isn't, ask whether to commit or stash first. Don't stash silently.
3. **Find the base.** Default to `git merge-base HEAD main` (fall back to `master` and then `origin/HEAD`). If the user named a base or a commit count, use that. Show the base commit you picked.
4. **On a feature branch.** `git branch --show-current` must print a branch name (empty means detached HEAD: stop and ask the user to check out or create a branch first), and it must not be `main`/`master`/`develop` or whatever the default branch is. Refuse to rewrite those.
5. **Check if the branch is published.** `git rev-parse --abbrev-ref @{u}` and `git log @{u}..HEAD` / `git log HEAD..@{u}`. If the commits have already been pushed, tell the user that the rewrite will need a force-push (`--force-with-lease`), and that anyone else who pulled the branch will have to reset. Continue only if they're fine with that. Never push yourself unless asked. If `@{u}` fails with "no upstream configured", the branch is unpublished: continue.
6. **Commit signing.** Rewriting drops existing signatures: if `git log --show-signature <base>..HEAD` shows signed commits, point this out. If `git config commit.gpgsign` prints `true`, every commit the rebase creates gets signed, and a passphrase prompt can hang or break the rebase. Tell the user, and ask them either to unlock their key in its agent first, or to agree to run without signing. In that case, use `git -c commit.gpgsign=false` for the rebase and for the `git commit` / `git rebase --continue` commands during a split. The new commits will then be unsigned.

## Phase 2: Read the history

Collect enough to understand each commit's *intent*, not just its message:

```bash
git log --reverse --stat --date=short --format='--- %h %ad %s%n%b' <base>..HEAD
```

For commits whose purpose isn't clear from the message and stat, look at the actual diff (`git show <sha>`). With many commits, read the diffs of the unclear ones only. Note:

- Merge commits in the range (`git log --merges <base>..HEAD`): see Edge cases.

If the branch has only one or two commits, and they are already clean, say so and propose nothing, or at most a reword. Not every branch needs rewriting.

## Phase 3: Classify and cluster

### Identify noisy commits

A commit is noise when it doesn't stand on its own as a meaningful change for a reviewer. Typical signs:

- Messages like `wip`, `tmp`, `fix`, `oops`, `typo`, `address review`, `fixup!`/`squash!`
- Fixes to something introduced *earlier on the same branch*. These belong inside the commit that introduced the bug.
- Revert pairs that cancel each other out. Drop both, but only if `git diff -U0 A^ A | git patch-id` and `git diff -U0 B B^ | git patch-id` print the same id (A is the original, B the revert). Don't judge by the message alone.
- Formatting or lint-only commits that touch only lines changed earlier on the branch
- "Merge main into feature" commits

Noise isn't discarded: its changes are folded into the logical commit they belong to.

### Cluster into logical changes

Group commits by what they accomplish together, using these signals, strongest first:

1. The same feature, task or ticket (shared task references, the same spec item)
2. One commit fixing or completing another (fixes to earlier commits are noise, see above)
3. Overlapping files or modules, plus the same kind of change (e.g. all dependency bumps)
4. Being close in time (only to break ties)

Good clusters are **independently reviewable** (one idea per commit), **in dependency order** (refactors and build changes before the features that need them, tests with or right after the code they test), and **ideally buildable at every step**. Don't merge unrelated changes just to reduce the commit count. Five clear commits beat one blob. Equally, a commit that is already clean and well-scoped should be left alone, because touching it adds risk for no gain.

A single commit that mixes two unrelated changes can be **split**, but only along file boundaries (see Phase 5). If a split needs hunk-level separation inside one file, say so in the plan and suggest leaving it combined.

Reordering is where conflicts happen. Prefer squashing commits that are already adjacent, and reorder only when it clearly improves the story. Before proposing a move, check whether the commits involved touch the same files (`git show --stat`). If they do, call it out as a conflict risk in the plan.

## Phase 4: Propose the plan and wait for approval

Present the plan in this format:

```
Base: <sha> (<branch/description>)   Commits on branch: N  →  Proposed: M

Proposed history (oldest first):

1. feat(auth): add password reset flow
   ← 3f2a1b7 feat: add password reset... (task 4.1)
   ← 8c9d0e1 fix typo in ResetForm              [noise: typo fix of 3f2a1b7]
   ← 5b6c7d8 wip                                [noise: completes 3f2a1b7]

2. fix(api): retry token refresh on timeout
   ← a4e5f60 (split: client.ts, retry.ts only)
...

Dropped: none            (or: list with the reason, e.g. "revert pair, net diff empty")
Conflict risk: <none | which moves may conflict and why>
Message style: <what you matched, e.g. "type(scope): lowercase imperative, task refs kept in parentheses">
```

Below the list, add the full message (subject and body) for any commit that gets a body.

### Writing the messages

Use Conventional Commits: `type(scope): summary`, matching what the repo already does. Check `git log -30 --format=%s <base>` for scopes, casing, whether task or issue references appear in parentheses, and whether `!` marks breaking changes, so the new messages look like they belong.

- **type**: a standard Conventional Commits type (`feat`, `fix`, `refactor`, `docs`, `chore`, …). Choose by what the change does for a user of the code. A bug fix discovered while building a feature is part of the `feat` if the bug never shipped.
- **scope**: optional. Use the module or area the repo already uses for scopes. Leave it out rather than invent a vague one.
- **summary**: imperative mood ("add", not "added"), lowercase start, no trailing period, aiming for 72 characters or fewer
- **`!` / `BREAKING CHANGE:`**: only for real breaking changes (removed public API, schema change without migration, and so on)
- **body**: optional. Explain *why* and anything non-obvious, wrapped at about 72 characters. Keep references that matter (issue numbers, spec task ids).

Ask the user to approve, edit or reject the plan. Apply their edits and show the revised plan again if the changes are substantial.

## Phase 5: Execute

### 5.1 Back up

```bash
git branch backup/<branch>-<YYYYMMDD-HHMM> HEAD && git rev-parse --verify --quiet backup/<branch>-<YYYYMMDD-HHMM>
```

If either command fails, stop: don't start the rebase without a backup. Otherwise tell the user the backup branch name. This is the undo button: `git reset --hard backup/...`.

### 5.2 Write the message files and the todo list

Put each new commit message in its own file in a scratch or temp directory outside the repo (e.g. `<scratch>/msg-01.txt`), so nothing gets committed by accident. Each message file must repeat every trailer (`Co-Authored-By`, `Signed-off-by`, …) from all the commits it replaces, without duplicates, because `fixup` and `--amend -F` drop them otherwise: `git log --no-walk --format='%(trailers:only,unfold)' <shas> | sort -u` lists them. Don't add new trailers, including a Claude `Co-Authored-By`: the rewrite regroups existing work and doesn't change who wrote it. Then write the rebase todo. Each line is `<command> <sha> <comment>`, oldest first:

| Goal | Todo lines |
|---|---|
| Keep as is | `pick <sha>` |
| Keep, new message | `pick <sha>` then `exec git commit --amend --quiet -F "<msgfile>"` |
| Squash several into one | `pick <first>`, `fixup <sha>` for each of the others, then `exec git commit --amend --quiet -F "<msgfile>"` |
| Drop (net-empty revert pairs only) | `drop <sha>` |
| Reorder | put the lines in the new order |
| Split by files | `edit <sha>` (you'll split when the rebase stops there, see 5.4). No `exec … --amend` after it: the split commits get their messages in 5.4, and an amend would overwrite the last one's. |

Use `fixup`, not `squash`: `squash` opens an editor to combine messages. The `exec ... --amend -F` line sets the final message instead. Use forward slashes and quotes in message file paths, because the exec lines run in git's shell.

Example todo:

```
# squash three commits into one, then set its message
pick 3f2a1b7 feat: add password reset
fixup 8c9d0e1 fix typo in ResetForm
fixup 5b6c7d8 wip
exec git commit --amend --quiet -F "/tmp/scratch/msg-01.txt"

# stop here to split this commit by files (5.4)
edit a4e5f60 fix: retry token refresh...

# keep this commit, only change its message
pick c7d8e9f feat: add reset email template
exec git commit --amend --quiet -F "/tmp/scratch/msg-03.txt"
```

Git skips lines starting with `#`, so the comments are safe to keep in the real todo.

### 5.3 Run the rebase

Replace git's generated todo with yours:

```bash
GIT_SEQUENCE_EDITOR="cp '<todo-file>'" GIT_EDITOR=true git rebase -i <base>
```

(Git runs this command through its own `sh`, so `cp` works on every platform. Run it from a POSIX shell such as Git Bash: in PowerShell, `$env:` variables would stay set for later rebases.)

### 5.4 Handle stops

- **`edit` stop (split):** the commit is applied. List its paths first with `git show --name-status -M HEAD`: a rename shows as `R<score> <old> <new>`, and both paths must go into the same group. Then undo the commit while keeping the changes in the working tree, and commit file groups in the planned order:
  ```bash
  git reset --quiet HEAD~
  git add <files of group 1> && git commit --quiet -F "<msg-a>"
  git add <files of group 2> && git commit --quiet -F "<msg-b>"
  git status --porcelain     # must be empty before continuing
  GIT_EDITOR=true git rebase --continue
  ```
- **Conflict:** don't guess at resolutions. Run `git rebase --abort` (this returns the branch to its original state), tell the user which commits conflicted and why, and propose a revised plan, usually one that keeps the original order for those commits. Resolve a conflict yourself only if the user asks you to, and show them the resolution.
- **An `exec` line failed:** usually a bad message file path. Fix it, and if the amend didn't happen, run the amend by hand, then `git rebase --continue`.
- **A hook rejected a commit:** the `exec` amends and the split commits run the repo's `pre-commit` and `commit-msg` hooks. Show the user the hook output and ask before retrying with `--no-verify`. Never bypass hooks on your own.
- **Anything else unexpected:** don't improvise. While the rebase is still running, `git rebase --abort` returns the branch to its original state. If it has already finished with a wrong result, restore the backup from 5.1. Then tell the user what happened.

### 5.5 Verify

The rewrite must not change the code, so check that before presenting anything:

```bash
git rev-parse HEAD^{tree} backup/<branch>-<stamp>^{tree}    # the two ids must match
```

If the ids differ, run `git diff --stat backup/<branch>-<stamp> HEAD` to see what changed, stop and report it, and offer to restore from the backup. Also check `git log <base>..HEAD` against the approved plan: commit count, order and messages.

If the project has a fast build or test command and the user wants it, offer to check that each commit builds: `git rebase --exec "<build cmd>" <base>`. This is optional, because it can be slow.

## Phase 6: Present the final history

Show before and after (the "before" is `git log --oneline <base>..backup/...`):

```
Before (N commits)                     After (M commits)
─────────────────────────────────────  ─────────────────────────────────────
3f2a1b7 feat: add password reset       1a2b3c4 feat(auth): add password res...
8c9d0e1 fix typo in ResetForm          9f8e7d6 fix(api): retry token refres...
5b6c7d8 wip                            ...
```

Then:
- Show what each new commit touches with `git log --oneline --stat <base>..HEAD`.
- Give the backup branch name. Suggest deleting the backup (`git branch -D backup/...`) once they're happy.
- If the branch was already pushed, remind them to run `git push --force-with-lease` (don't run it yourself).

## Edge cases

- **Merge commits in the range:** leave merges from the base branch out of the todo and rebase onto the Phase 1 merge-base as usual. It already includes everything the merges brought in, so the final tree stays identical. Replaying may hit conflicts that the merge had resolved (see 5.4). If another feature branch was merged in, stop and discuss rather than flattening it.
- **Very long branches (more than 40 commits):** cluster in passes and present the plan in sections, so the user can review it in parts.
- **The user asks "just squash everything":** that's a valid plan. Still confirm, still back up, and still write one good conventional message that summarizes the whole branch.
