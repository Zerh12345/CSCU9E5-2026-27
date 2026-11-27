# Three-way traffic lights — R1–R10

```mermaid
stateDiagram-v2
  [*] --> stopped
  stopped --> W_r: run
  state "W: red / all red" as W_r
  state "W: red+amber" as W_ra
  state "W: green" as W_g
  state "W: amber" as W_a
  state "N: red / all red" as N_r
  state "N: red+amber" as N_ra
  state "N: green" as N_g
  state "N: amber" as N_a
  state "S: red / all red" as S_r
  state "S: red+amber" as S_ra
  state "S: green" as S_g
  state "S: amber" as S_a
  W_r --> W_ra: next
  W_ra --> W_g: next
  W_g --> W_a: next
  W_a --> N_r: next
  N_r --> N_ra: next
  N_ra --> N_g: next
  N_g --> N_a: next
  N_a --> S_r: next
  S_r --> S_ra: next
  S_ra --> S_g: next
  S_g --> S_a: next
  S_a --> W_r: next
  state "Stopping W: red+amber" as SW_ra
  state "Stopping W: green" as SW_g
  state "Stopping W: amber" as SW_a
  state "Stopping N: red+amber" as SN_ra
  state "Stopping N: green" as SN_g
  state "Stopping N: amber" as SN_a
  state "Stopping S: red+amber" as SS_ra
  state "Stopping S: green" as SS_g
  state "Stopping S: amber" as SS_a
  W_r --> stopped: halt
  N_r --> stopped: halt
  S_r --> stopped: halt
  W_ra --> SW_ra: halt
  W_g --> SW_g: halt
  W_a --> SW_a: halt
  N_ra --> SN_ra: halt
  N_g --> SN_g: halt
  N_a --> SN_a: halt
  S_ra --> SS_ra: halt
  S_g --> SS_g: halt
  S_a --> SS_a: halt
  SW_ra --> SW_g: next
  SW_g --> SW_a: next
  SW_a --> stopped: next
  SN_ra --> SN_g: next
  SN_g --> SN_a: next
  SN_a --> stopped: next
  SS_ra --> SS_g: next
  SS_g --> SS_a: next
  SS_a --> stopped: next
```

- Only the currently selected approach is permitted to show `:ra`, `:g`, or `:a`; others remain red.
- Round-robin `:w` → `:n` → `:s` gives each approach a green phase every twelve `:next` events.
- The stopping subcharts preserve the current colour on `:halt` and return to all-red via `:next`.
