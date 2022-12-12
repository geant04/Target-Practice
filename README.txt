=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: geant
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. Collections and/or Maps

  2. File I/O

  3. Inheritance and Subtyping

  4. JUnit Testable Component

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
    TargetCourt -
    represents the JPanel and the main visual of the game, as well as all the coding
    behind gameplay such as the game loop, spawning, etc.

    RunTarget -
    build the JFrame and main buttons to make the game playable

    TargetObj -
    Main target object that all the different types of targets will use. Sub types will
    override the methods "move" and etc. to modify how they act in game.

    Normal -
    Normal target, does pretty much nothing but act as a regular target.

    Ghost -
    Disappears upon spawning but appears after reaching the apex of its trajectory

    Fast -
    Normal target but usually faster and smaller in size.

    DoubleTarget -
    Normal target that needs two click to beat; after clicked once, it jumps up with some velocity

    Plate -
    Detail target, only exists for "particle effects"

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

    No significant stumbling blocks since the implementation was designed beforehand, so
    the only modifications I had to makew with the objects were very minor variable type
    adjustments or just the additions of other fields.

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

    I think there's a decent amount of functionality, but the private state is not
    well encapsulated since I just use getters and setters. I would organize my
    code in the TargetCourt a lot better, mainly by splitting the different functions
    in other files like one class dedicated to holding methods for spawning and other
    game mechanics used by the game loop.


========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

  Target asset: https://www.pngaaa.com/detail/2442722
  Tutorial for implementing Audio : Java docs,
    https://docs.oracle.com/javase/7/docs/api/javax/sound/sampled/AudioInputStream.html
  Text in game : Java docs, (part of Swing)
