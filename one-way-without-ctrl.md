# One-way without controller — R1–R8

```mermaid
stateDiagram-v2
  [*] --> r
  r --> ra: next
  ra --> g: next
  g --> a: next
  a --> r: next
```

- `:r`, `:ra`, `:g`, `:a` stand for red, red+amber, green and amber.
- This configuration has no `:run`/`:halt` controller; every legal colour transition uses `:next`.
