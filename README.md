# CSCU9E5 Assignment 2 — Traffic lights

Three clj-statecharts specifications, Mermaid diagrams and Kaocha verification suites.

## Run and verify

From the repository root, with Java and Clojure CLI installed:

```sh
clj -M:test
```

Alternatively, in an IntelliJ/Cursive REPL using the project classpath with the test dependencies:

```clojure
(require 'kaocha.repl)
(kaocha.repl/run-all)
```

## Verification record

On 27 November 2026, the student ran the corrected specifications and tests in IntelliJ/Cursive with Java 21, Clojure 1.12.6, clj-statecharts 0.1.7 and Kaocha 1.91.1392. The reported result was:

```text
5 tests, 250 assertions, 0 failures.
=> #:kaocha.result {:count 5, :pass 250, :error 0, :fail 0, :pending 0}
```

This archive reconstructs the four corrections applied in that session and updates the one-way diagram. Re-run the tests on the extracted archive to confirm the reconstruction. Passing tests establish the checked behaviours, not an exhaustive proof.

## Design rationale

- Display colours change only on `:next`. A halt request preserves the current colour and subsequent next events complete the UK cycle to red.
- The uncontrolled signal rejects unsupported control events. The one-way controller accepts `:next` while stopped and repeated `:run` while running without changing the display.
- Three-way uses a fair west, north, south round-robin schedule. Only one approach is non-red at a time. With a separate all-red handover and one visible signal change per event, each approach receives green every 12 next events. This is minimal under those additional design constraints, not a proof of global optimality under R1–R10 alone.
- Unspecified events may be rejected; continued next events are assumed for liveness.

## Files

- `src/e5`: three specifications
- `doc`: three Mermaid state diagrams
- `test/e5`: three verification namespaces
- `deps.edn`, `tests.edn`: original configuration

Review and curate the work in accordance with AIAS Level 3 before committing it to Gitea. Submit the actual final commit hash in Canvas.
