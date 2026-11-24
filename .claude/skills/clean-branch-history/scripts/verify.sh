#!/usr/bin/env bash
# Verifies the automatically checkable promises of the clean-branch-history skill
# after it has run. Read-only: it never modifies branches, the index or the working tree.
#
# Usage: verify.sh [--base <ref>] [--backup <branch>] [--expect-count N]
#                  [--build-cmd "<cmd>"] [--allow-types "a|b|c"]
#
#   --base          base of the rewritten range (default: merge-base HEAD main|master|origin/HEAD)
#   --backup        backup branch (default: newest backup/<current-branch>-* branch)
#   --expect-count  number of commits the approved plan promised
#   --build-cmd     run in a temp worktree at every new commit (e.g. "./gradlew assembleDebug")
#   --allow-types   Conventional Commit types (default: the standard set)
#
# Exit code: 0 = no failures (warnings allowed), 1 = at least one failure, 2 = usage/setup error.
#
# Example (run from the repo root in Git Bash, on the rewritten branch):
#
#   slow option:
#
#   bash .claude/skills/clean-branch-history/scripts/verify.sh \
#     --expect-count 10 \
#     --build-cmd "./gradlew assembleDebug"
#
#   fast option:
#
#   bash .claude/skills/clean-branch-history/scripts/verify.sh \
#     --expect-count 10 \
#     --build-cmd "./gradlew compileDebugKotlin"
#
# --base is the tip of main before the rewrite (or just "main"). --build-cmd runs a
# cold build at every new commit in a temporary worktree, so it is slow; use
# "./gradlew compileDebugKotlin" for a quicker compile-only check.

set -u

BASE="" BACKUP="" EXPECT="" BUILD="" TYPES="feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert"
while [ $# -gt 0 ]; do
  case "$1" in
    --base) BASE="$2"; shift 2 ;;
    --backup) BACKUP="$2"; shift 2 ;;
    --expect-count) EXPECT="$2"; shift 2 ;;
    --build-cmd) BUILD="$2"; shift 2 ;;
    --allow-types) TYPES="$2"; shift 2 ;;
    -h|--help) sed -n '2,/^[^#]/{/^#/s/^# \{0,1\}//p}' "$0"; exit 0 ;;
    *) echo "unknown argument: $1" >&2; exit 2 ;;
  esac
done

PASS=0 FAIL=0 WARN=0
pass() { PASS=$((PASS+1)); printf '  PASS  %s\n' "$1"; }
fail() { FAIL=$((FAIL+1)); printf '  FAIL  %s\n' "$1"; }
warn() { WARN=$((WARN+1)); printf '  WARN  %s\n' "$1"; }
info() { printf '  info  %s\n' "$1"; }
section() { printf '\n%s\n' "$1"; }

git rev-parse --git-dir >/dev/null 2>&1 || { echo "not a git repository" >&2; exit 2; }

BRANCH=$(git branch --show-current)

# ---------------------------------------------------------------- setup
section "Setup"
if [ -z "$BRANCH" ]; then fail "HEAD is detached (the skill requires a named branch)"; echo; echo "cannot continue"; exit 1; fi

DEFAULT=""
for cand in main master; do
  git rev-parse --verify --quiet "$cand" >/dev/null && { DEFAULT=$cand; break; }
done
[ -z "$DEFAULT" ] && DEFAULT=$(git symbolic-ref --quiet --short refs/remotes/origin/HEAD 2>/dev/null | sed 's#^origin/##')

if [ -z "$BASE" ]; then
  for cand in main master origin/HEAD; do
    BASE=$(git merge-base HEAD "$cand" 2>/dev/null) && [ -n "$BASE" ] && break
    BASE=""
  done
fi
[ -z "$BASE" ] && { echo "cannot determine base, pass --base" >&2; exit 2; }
BASE=$(git rev-parse "$BASE^{commit}") || exit 2

if [ -z "$BACKUP" ]; then
  BACKUP=$(git for-each-ref --sort=-creatordate --format='%(refname:short)' "refs/heads/backup/$BRANCH-*" | head -n1)
fi
[ -z "$BACKUP" ] && { fail "no backup branch backup/$BRANCH-* found"; echo; echo "cannot continue"; exit 1; }

info "branch: $BRANCH"
info "base:   $(git log -1 --format='%h %s' "$BASE")"
info "backup: $BACKUP ($(git rev-parse --short "$BACKUP"))"

# ---------------------------------------------------------------- pre/post-flight state
section "Repository state"
RM=$(git rev-parse --git-path rebase-merge); RA=$(git rev-parse --git-path rebase-apply)
if [ -e "$RM" ] || [ -e "$RA" ]; then fail "a rebase is still in progress"; else pass "no rebase in progress"; fi

if [ -z "$(git status --porcelain)" ]; then pass "working tree clean (no leftover split/stash changes)"; else fail "working tree not clean"; git status --short | sed 's/^/          /'; fi

case "$BRANCH" in
  main|master|develop) fail "current branch is protected ($BRANCH)" ;;
  *) [ "$BRANCH" = "$DEFAULT" ] && fail "current branch is the default branch" || pass "on a feature branch" ;;
esac

# ---------------------------------------------------------------- backup
section "Backup"
git rev-parse --verify --quiet "$BACKUP" >/dev/null && pass "backup branch resolves" || fail "backup branch does not resolve"
if [ "$(git merge-base "$BACKUP" HEAD)" = "$BASE" ] || git merge-base --is-ancestor "$BASE" "$BACKUP"; then
  pass "backup is built on the same base"
else
  fail "backup does not contain the base: wrong backup or base?"
fi
if [ "$(git rev-parse HEAD)" = "$(git rev-parse "$BACKUP")" ]; then
  warn "HEAD equals backup: history was not rewritten (fine only if the plan was 'no change')"
fi

# ---------------------------------------------------------------- core promise: identical code
section "Core promise: final tree is byte-identical"
T_NEW=$(git rev-parse 'HEAD^{tree}'); T_OLD=$(git rev-parse "$BACKUP^{tree}")
if [ "$T_NEW" = "$T_OLD" ]; then pass "tree ids match ($T_NEW)"; else fail "tree differs from backup"; git diff --stat "$BACKUP" HEAD | sed 's/^/          /'; fi

# ---------------------------------------------------------------- shape of history
section "Shape of history"
N_OLD=$(git rev-list --count "$BASE..$BACKUP"); N_NEW=$(git rev-list --count "$BASE..HEAD")
info "commits: $N_OLD before -> $N_NEW after"
[ "$N_NEW" -gt 0 ] && pass "branch still has commits" || fail "no commits left on branch"
[ "$N_NEW" -le "$N_OLD" ] && pass "commit count did not grow" || warn "commit count grew ($N_OLD -> $N_NEW): expected only if a split was planned"
if [ -n "$EXPECT" ]; then
  [ "$N_NEW" -eq "$EXPECT" ] && pass "commit count matches approved plan ($EXPECT)" || fail "commit count $N_NEW != planned $EXPECT"
fi
if [ -z "$(git rev-list --merges "$BASE..HEAD")" ]; then pass "no merge commits in rewritten range"; else fail "merge commits remain in range"; fi
if git merge-base --is-ancestor "$BASE" HEAD; then pass "rewritten range sits directly on the base"; else fail "base is not an ancestor of HEAD"; fi

# ---------------------------------------------------------------- per-commit checks
section "Per-commit checks (oldest first)"
RE="^($TYPES)(\([a-z0-9._/-]+\))?!?: [^A-Z[:space:]].*$"
NOISE='^(wip|tmp|fix|oops|typo|fixup!|squash!|address review|merge )'
i=0
for sha in $(git rev-list --reverse "$BASE..HEAD"); do
  i=$((i+1)); short=$(git rev-parse --short "$sha")
  subj=$(git log -1 --format=%s "$sha")
  printf '  [%d] %s %s\n' "$i" "$short" "$subj"
  if printf '%s' "$subj" | grep -Eq "$RE"; then pass "conventional commit format"; else fail "subject is not 'type(scope): lowercase summary'"; fi
  [ ${#subj} -le 72 ] && pass "subject <= 72 chars (${#subj})" || warn "subject is ${#subj} chars (> 72)"
  case "$subj" in *.) fail "subject ends with a period" ;; esac
  if printf '%s' "$subj" | grep -Eiq "$NOISE"; then fail "subject still looks like noise (wip/fixup/merge...)"; fi
  word=$(printf '%s' "${subj#*: }" | awk '{print tolower($1)}')
  case "$word" in
    *ed|*ing) warn "first word '$word' may not be imperative" ;;
  esac
  [ -n "$(git diff-tree --no-commit-id --name-only -r "$sha")" ] && pass "commit is not empty" || fail "empty commit"
  long=$(git log -1 --format=%b "$sha" | grep -Ev '^[A-Za-z-]+: ' | awk 'length>72' | wc -l)
  [ "$long" -eq 0 ] && pass "body lines wrapped at 72" || warn "$long body line(s) longer than 72 chars"
done

# ---------------------------------------------------------------- trailers & authorship
section "Trailers and authorship"
trailers() { git log --format='%(trailers:only,unfold)' "$1" | sed '/^$/d' | sort -u; }
OLD_T=$(trailers "$BASE..$BACKUP"); NEW_T=$(trailers "$BASE..HEAD")
LOST=$(comm -23 <(printf '%s\n' "$OLD_T") <(printf '%s\n' "$NEW_T") | sed '/^$/d')
ADDED=$(comm -13 <(printf '%s\n' "$OLD_T") <(printf '%s\n' "$NEW_T") | sed '/^$/d')
[ -z "$LOST" ] && pass "no trailer lost (Co-Authored-By, Signed-off-by...)" || { fail "trailers lost:"; printf '%s\n' "$LOST" | sed 's/^/          /'; }
[ -z "$ADDED" ] && pass "no trailer added (e.g. no new Claude Co-Authored-By)" || { fail "trailers added:"; printf '%s\n' "$ADDED" | sed 's/^/          /'; }

OLD_A=$(git log --format='%an <%ae>' "$BASE..$BACKUP" | sort -u); NEW_A=$(git log --format='%an <%ae>' "$BASE..HEAD" | sort -u)
BAD_A=$(comm -13 <(printf '%s\n' "$OLD_A") <(printf '%s\n' "$NEW_A"))
[ -z "$BAD_A" ] && pass "all authors existed in the original history" || { fail "new authors appeared:"; printf '%s\n' "$BAD_A" | sed 's/^/          /'; }

# ---------------------------------------------------------------- per-file attribution
section "Content accounting"
OLD_F=$(git diff --name-only "$BASE" "$BACKUP" | sort); NEW_F=$(git diff --name-only "$BASE" HEAD | sort)
[ "$OLD_F" = "$NEW_F" ] && pass "same set of files changed vs base" || fail "set of changed files differs from backup"
DUP=$(git log --format= --name-only "$BASE..HEAD" | sed '/^$/d' | sort | uniq -d | wc -l)
info "$DUP file(s) touched by more than one new commit (informational)"

# ---------------------------------------------------------------- publication / signing
section "Publication and signing"
if UP=$(git rev-parse --abbrev-ref '@{u}' 2>/dev/null); then
  if [ -n "$(git log '@{u}..HEAD' --format=%h)$(git log 'HEAD..@{u}' --format=%h)" ] && ! git merge-base --is-ancestor '@{u}' HEAD; then
    warn "diverged from $UP: a push needs --force-with-lease (the skill must not have pushed)"
  else
    info "upstream $UP is an ancestor of HEAD or equal"
  fi
else
  pass "no upstream: branch unpublished, nothing could have been pushed"
fi
UNSIGNED=$(git log --format='%G?' "$BASE..HEAD" | grep -vc '^[GU]$' || true)
[ "$(git config commit.gpgsign)" = "true" ] && info "commit.gpgsign=true; $UNSIGNED unsigned commit(s)" || info "signing not enabled"

# ---------------------------------------------------------------- optional: builds at every commit
if [ -n "$BUILD" ]; then
  section "Build at every commit: $BUILD"
  WT=$(mktemp -d); trap 'git worktree remove --force "$WT" >/dev/null 2>&1; rm -rf "$WT"' EXIT
  git worktree add --quiet --detach "$WT" HEAD || { fail "could not create temp worktree"; exit 1; }
  for sha in $(git rev-list --reverse "$BASE..HEAD"); do
    ( cd "$WT" && git checkout --quiet --detach "$sha" && eval "$BUILD" >/dev/null 2>&1 ) \
      && pass "builds at $(git rev-parse --short "$sha")" || fail "build fails at $(git rev-parse --short "$sha")"
  done
fi

# ---------------------------------------------------------------- summary
section "Summary"
printf '  %d passed, %d failed, %d warnings\n' "$PASS" "$FAIL" "$WARN"
printf '\nNot automatable (review by hand): are the groupings logical, are messages accurate,\n'
printf 'did the user approve the plan before execution, was history shown as before/after.\n'
[ "$FAIL" -eq 0 ]
