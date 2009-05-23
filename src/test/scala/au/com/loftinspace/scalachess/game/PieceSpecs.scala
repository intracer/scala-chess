package au.com.loftinspace.scalachess.game

import org.specs._
import org.scalacheck.Prop._
import Positioning._

object PieceSpec extends Specification with SystemContexts {
  "a piece that is taken" should {
    val takenPiece = systemContext {
      val game = new Game
      val blackQueen = Piece(Black, Queen)
      val whiteQueen = Piece(White, Queen)
      game place whiteQueen at 'e5
      game place blackQueen at 'e5
      Tuple3(game, blackQueen, whiteQueen)
    }

    "be marked as captured".withA(takenPiece) { scenario =>
      scenario._3.captured must beTrue
    }
  }

  "a pawn that has not yet moved" should {
    val newGame = systemContext {new Game}
    "be able to move either one or two spaces towards opposite colour".withA(newGame) { game =>
      val pawnsLocs = for (file <- 'a' to 'h'; rank <- List('2', '7')) yield position(Symbol(file.toString + rank))
      pawnsLocs.foreach { loc =>
        val pawn = (game pieceAt loc).get
        val direction = if (loc.rank.equals(2)) 1 else -1
        val expected = Set(loc ^ direction, loc ^ direction*2).map(_.get)
        game findMovesFor pawn must containAll(expected)
      }
    }
  }

  "a pawn that has not yet moved and is blocked" should {
    val blockedPawnGame = systemContext {
      val game = new Game
      game place (game pieceAt 'e2 get) at 'e6
      game
    }

    "not be able to move anywhere".withA(blockedPawnGame) { game =>
      val pawn = (game pieceAt 'e7).get
      game findMovesFor pawn must beEmpty
    }
  }

  "a pawn that has not yet moved and is blocked from moving 2 squares" should {
    val blockedPawnGame = systemContext {
      val game = new Game
      game place(game pieceAt 'e2 get) at 'e5
      game
    }

    "be able to move one square only".withA(blockedPawnGame) { game =>
      val pawn = (game pieceAt 'e7).get
      game findMovesFor pawn must containPositions('e6)
    }
  }

  "a pawn that has moved and is not blocked" should {
    val blockedPawnGame = systemContext {
      val game = new Game
      game move 'e2 to 'e4
      game
    }

    "be able to move 1 square forward".withA(blockedPawnGame) { game =>
      val pawn = (game pieceAt 'e4).get
      game findMovesFor pawn must containPositions('e5)
    }
  }

  "a pawn that has opposing pieces on the forward diagonals" should {
    val pawnCanTakeGame = systemContext {
      val game = new Game
      game move 'e2 to 'e4
      game place (Piece(Black, Rook)) at 'd5
      game place (Piece(Black, Pawn)) at 'f5
      game
    }

    "be able to move 1 square forward or take to either diagonal".withA(pawnCanTakeGame) { game =>
      val pawn = (game pieceAt 'e4).get
      game findMovesFor pawn must containPositions('e5, 'd5, 'f5)
    }
  }

  "a pawn at the edge of the board that has an opposing piece on a forward diagonal" should {
    val pawnCanTakeGame = systemContext {
      val game = new Game
      game move 'a2 to 'a4
      game place (Piece(Black, Rook)) at 'b5
      game
    }

    "be able to move 1 square forward or take the diagonal".withA(pawnCanTakeGame) { game =>
      val pawn = (game pieceAt 'a4).get
      game findMovesFor pawn must containPositions('a5, 'b5)
    }
  }

  "a pawn that is blocked from going forward but has an opposing piece on the diagonal" should {
    val blockedPawnCanTakeGame = systemContext {
      val game = new Game
      game move 'f7 to 'f5
      game place (Piece(Black, Pawn)) at 'f4
      game place (Piece(White, Queen)) at 'e4
      game
    }

    "be able to take on the diagonal only".withA(blockedPawnCanTakeGame) { game =>
      val pawn = (game pieceAt 'f5).get
      game findMovesFor pawn must containPositions('e4)
    }
  }

  "a pawn that has same side pieces on the forward diagonals" should {
    val pawnCanTakeGame = systemContext {
      val game = new Game
      game move 'e2 to 'e4
      game place (Piece(White, Rook)) at 'd5
      game place (Piece(White, Pawn)) at 'f5
      game
    }

    "be able to move 1 square forward only".withA(pawnCanTakeGame) { game =>
      val pawn = (game pieceAt 'e4).get
      game findMovesFor pawn must containPositions('e5)
    }
  }

  "a pawn that is adjacent to an opposing pawn that has just moved 2 places" should {
    val pawnEnPassantGame = systemContext {
      val game = new Game
      game move 'e2 to 'e4
      game place (Piece(Black, Pawn)) at 'f4
      game
    }

    "be able to perform en passant".withA(pawnEnPassantGame) { game =>
      val pawn = (game pieceAt 'f4).get
      game findMovesFor pawn must containPositions('e3, 'f3)
    }
  }

  private def containPositions(s: Symbol*) = containAll(s.map(position(_)))
}
