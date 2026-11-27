# One-way with controller — R1–R10

```mermaid
stateDiagram-v2
  [*] --> stopped
  stopped --> stopped: next
  stopped --> running_r: run
  state "running: red" as running_r
  state "running: red+amber" as running_ra
  state "running: green" as running_g
  state "running: amber" as running_a
  state "stopping: red+amber" as stopping_ra
  state "stopping: green" as stopping_g
  state "stopping: amber" as stopping_a
  running_r --> running_r: run
  running_ra --> running_ra: run
  running_g --> running_g: run
  running_a --> running_a: run
  running_r --> running_ra: next
  running_ra --> running_g: next
  running_g --> running_a: next
  running_a --> running_r: next
  running_r --> stopped: halt
  running_ra --> stopping_ra: halt
  running_g --> stopping_g: halt
  running_a --> stopping_a: halt
  stopping_ra --> stopping_g: next
  stopping_g --> stopping_a: next
  stopping_a --> stopped: next
```

- `:run` changes controller mode while retaining red; `:halt` changes mode without changing lamp colour.
- Once halting is requested, legal `:next` events complete the remaining display cycle to red.
- Running-state `:run` loops depict internal, colour-preserving transitions; `:next` while stopped leaves the signal red.
