# Contributing

## Updating screenshot goldens

`verifyRoborazziDebug` compares against the committed goldens at an exact match. If your
change moves pixels on purpose, re-record and commit the goldens in the same PR:

    ./gradlew recordRoborazziDebug

Recording locally is safe because CI re-renders every PR on its pinned runner and rejects any
golden that is not pixel-identical to that render. A recording from an environment that
renders differently cannot merge green -- it turns the check red, and the CI run's
`roborazzi-comparisons` artifact shows the difference. If that happens to you, say so on the
PR; a maintainer will record on CI instead.

Record after seeing the red, not preemptively: the failing run's artifact is the before/after
you are accepting.
