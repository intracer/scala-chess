package au.com.loftinspace.scalachess.game

import org.specs._

object GameSpec extends Specification with SystemContexts {

  "a game" should {
    val game = systemContext{ new Game }

    "allow a piece to be placed anywhere on the board".withA(game) { game =>
      val pawn = new Piece(White, Pawn)
      val allCoordinates = for {
        file <- 'a' to 'h'
        row <- 1 to 8
      } yield Symbol(file.toString + row)
      allCoordinates.foreach { coord =>
        game place pawn at coord must beNone
      }
    }

    "report what piece is at any coordinate".withA(game) { game =>
      val pawn = new Piece(Black, Pawn)
      game place pawn at 'e6
      game pieceAt 'e6 must beSome[Piece].which(_.equals(pawn))
      game pieceAt 'f6 must beNone
    }

    "have no more than one reference to the same piece".withA(game) { game =>
      val pawn = new Piece(Black, Pawn)
      game place pawn at 'd7
      game place pawn at 'd6
      game pieceAt 'd7 must beNone
      game pieceAt 'd6 must beSome[Piece].which(_.equals(pawn))
    }

    "provide the taken piece, if any, when a move is made".withA(game) { game =>
      val queen = new Piece(Black, Queen)
      val rook = new Piece(White, Rook)
      game place queen at 'e8 must beNone
      game place rook at 'e8 must beSome[Piece].which(_.equals(queen))
    }

    "reject any attempt to check a piece at a non-coordinate".withA(game) { game =>
      game pieceAt 'a0 must throwA[IllegalArgumentException]
      game pieceAt 'harvey must throwA[IllegalArgumentException]
      game pieceAt null must throwA[IllegalArgumentException]
    }
  }
}