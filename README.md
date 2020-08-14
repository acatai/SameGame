
This is a port of [SameGame](https://en.wikipedia.org/wiki/SameGame) to [CodinGame](https://www.codingame.com/). The rules are the same as used for benchmarks in AI publications.

# CodinGame
- **[Approved game](https://www.codingame.com/multiplayer/optimization/samegame)**
- [Github repository](https://github.com/acatai/SameGame)
- [Contribution page at CodinGame](https://www.codingame.com/contribute/view/52286850570a02e25f12d5b1b9a30bdb954c)

# Short game description
SameGame is a puzzle composed of a rectangular grid containing cells of different colors. A move removes connected cells of the same color. The cells of other colors fall to fill the void created by a move down, and when a column is empty the columns on the right are moved to the left. At least two cells have to be removed for a move to be legal. The score of a move is the square of the number of removed cells minus two. A bonus of one thousand is credited for completely clearing the board.

# Online play
- [js-games.de](http://www.js-games.de/eng/games/samegame/lx/play)

# Literature
- Stabilized Nested Rollout Policy Adaptation, Tristan Cazenave, Jean-Baptiste Sevestre and Matthieu Toulemont, MCS 2020
- [Generalized Nested Rollout Policy Adaptation](https://arxiv.org/pdf/2003.10024.pdf), Tristan Cazenave, MCS 2020
- [Distributed Nested Rollout Policy for Same Game](https://www.lamsade.dauphine.fr/~cazenave/papers/NegrevergneDistributed.pdf), Benjamin Negrevergne, Tristan Cazenave. CGW 2017
 - [Nested Rollout Policy Adaptation with Selective Policies](https://www.lamsade.dauphine.fr/~cazenave/papers/policyNRPA.pdf), Tristan Cazenave, CGW 2016
- [Single-Player Monte-Carlo Tree Search for SameGame](https://dke.maastrichtuniversity.nl/m.winands/documents/KNOSYS_SameGame.pdf), Schadd, M. P., Winands, M. H., Tak, M. J., & Uiterwijk, J. W, Knowledge-Based Systems 2012
- [The Complexity of Clickomania](https://arxiv.org/pdf/cs/0107031.pdf), Therese C. Biedl, Erik D. Demaine, Martin L. Demaine, Rudolf Fleischer, Lars Jacobsen, J. Ian Munro, 2001
