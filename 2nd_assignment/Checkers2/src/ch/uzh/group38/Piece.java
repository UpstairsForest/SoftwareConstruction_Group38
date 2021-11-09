package ch.uzh.group38;


public class Piece {

    public enum Color {WHITE, RED}
    public enum Type {PAWN, KING}

    private final Color color;
    private Type type;

    public Piece (Color c, Type t){
        this.color = c;
        this.type = t;
    }

    /*
    return s String representation of the Piece for it to be printed
    */
    public String getLabel (){
        String label = "[";

        if (this.color==Color.RED){
            label += "R_";
        }
        else if (this.color==Color.WHITE){
            label += "W_";
        }

        if (this.type==Type.KING){
            label += "K] ";
        }
        else if (this.type==Type.PAWN){
            label += "P] ";
        }

        return label;
    }

    public void convertToKing(){
        if (this.type != Type.KING){
            System.out.print("Well done Player ");
            RuleEvaluator.printCurrentPlayer();
            System.out.print("! Your pawn is now a king!\n");
            this.type = Type.KING;
        }
    }

    public boolean isWhite(){
        return (this.color == Color.WHITE);
    }

    public boolean isRed(){
        return (this.color == Color.RED);
    }

    public boolean isKing(){
        return (this.type == Type.KING);
    }

}