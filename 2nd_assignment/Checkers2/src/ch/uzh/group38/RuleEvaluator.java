package ch.uzh.group38;


public class RuleEvaluator {

    /*
    variable to keep track of whose turn it is
    */
    private static int currentPlayer;

    //sets first Player to 1
    public static void resetCurrentPlayer(){
        currentPlayer = 1;
    }

    public static void printCurrentPlayer(){
        System.out.print(currentPlayer);
    }

    public static void updateTurn(Board board){
        checkWinner(board);
        //PLayer 1 is red, Player 2 is white
        if (currentPlayer == 1){
            currentPlayer = 2;
        }
        else{
            currentPlayer = 1;
        }
    }

    /*
    checks whether a given Move is a valid
    */
    public static boolean checkValidity(Move move, Board board){
        int x1 = move.FromX();
        int y1 = move.FromY();
        int y2 = move.ToY();

        //check if move is going in the right direction
        //red pawns can only move negative y
        if (board.getField(x1,y1).isRed() && !board.getField(x1,y1).isKing()
            && y2 - y1 >= 0 ){
                return false;
            }

        //white pawns can only move positive y
        if (board.getField(x1,y1).isWhite() && !board.getField(x1,y1).isKing()
            && y2 - y1 <= 0 ){
                return false;
            }

        //check if it is players color  
        if (((board.getField(x1,y1).isRed() && currentPlayer == 1)
            || (board.getField(x1,y1).isWhite() && currentPlayer == 2))){
                
            if (isJumpMove(move, board) || isSimpleMove(move, board)){
                //check if a jump move was possible
                if (isSimpleMove(move, board)){
                    for (int i = 0; i < 8; i++){
                        for (int j = 0; j < 8; j++){
                            if (currentPlayer == 1 && board.getField(i,j).isRed() || currentPlayer == 2 && board.getField(i,j).isWhite()){
                                if (checkForJumpMoves(i, j, board)){
                                    System.out.println("There is a possible jump move");
                                    return false;
                                }
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /*
    checks if there are possible jumpMoves from a position
    */
    public static boolean checkForJumpMoves(int x, int y, Board board){
        if (board.getField(x, y).isKing() || board.getField(x, y).isWhite()){
            if (x+2 < 8 && y+2 < 8){
                Move potentialMove = new Move(x, y, x+2, y+2);
                if (isJumpMove(potentialMove, board)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isWhite()){
            if (x-2 >= 0 && y+2 < 8) {
                Move potentialMove = new Move(x, y, x-2, y+2);
                if (isJumpMove(potentialMove, board)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isRed()){
            if (x-2 >= 0 && y-2 >= 0){
                Move potentialMove = new Move(x, y, x-2, y-2);
                if (isJumpMove(potentialMove, board)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isRed()){
            if (x+2 < 8 && y-2 >= 0){
                Move potentialMove = new Move(x, y, x+2, y-2);
                return isJumpMove(potentialMove, board);
            }
        }

        return false;
    }

    /*
    checks if there are possible simpleMoves from a position
    */
    private static boolean checkForSimpleMoves(int x, int y, Board board){
        if (board.getField(x, y).isKing() || board.getField(x, y).isWhite()){
            if (x+1 < 8 && y+1 < 8) {
                Move potentialMove = new Move(x, y, x+1, y+1);
                if (isSimpleMove(potentialMove, board)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isWhite()){
            if (x-1 >= 0 && y+1 < 8){
                Move potentialMove = new Move(x, y, x-1, y+1);
                if (isSimpleMove(potentialMove, board)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isRed()){
            if (x-1 >= 0 && y-1 >= 0){
                Move potentialMove = new Move(x, y, x-1, y-1);
                if (isSimpleMove(potentialMove, board)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isRed()){
            if (x+1 < 8 && y-1 >= 0){
                Move potentialMove = new Move(x, y, x+1, y-1);
                return isSimpleMove(potentialMove, board);
            }
        }

        return false;
    }

    /*
    checks whether a given Move is a jumpMove
    */
    public static boolean isJumpMove(Move move, Board board){
        int x1 = move.FromX();
        int y1 = move.FromY();
        int x2 = move.ToX();
        int y2 = move.ToY();

        if ((x1 - x2 == 2 || x1 - x2 == -2)
            && (y1 - y2 == 2 || y1 - y2 == -2) 
            && board.getField(x2,y2).isEmpty()){

            //check if jump is over opponent pin
            return (board.getField(x1, y1).isWhite() && (board.getField((x1 + x2) / 2, (y1 + y2) / 2)).isRed())
                    || (board.getField(x1, y1).isRed() && (board.getField((x1 + x2) / 2, (y1 + y2) / 2)).isWhite());
        }
        return false;
    }

    /*
    checks whether a given Move is a simpleMove
    */
    private static boolean isSimpleMove(Move move, Board board){
        int x1 = move.FromX();
        int y1 = move.FromY();
        int x2 = move.ToX();
        int y2 = move.ToY();

        return (x1 - x2 == 1 || x1 - x2 == -1)
                && (y1 - y2 == 1 || y1 - y2 == -1)
                && board.getField(x2, y2).isEmpty();
    }

    /*
    checks if a player has won the game
    */
    private static void checkWinner(Board board){
        //check if there are pins left and if they can move
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (currentPlayer == 1 && board.getField(i,j).isWhite() || (currentPlayer == 2 && board.getField(i,j).isRed())){
                    if (checkForJumpMoves(i, j,board) || checkForSimpleMoves(i, j,board)){
                        return;
                    }
                }
            }
        }
        System.out.println("Player " + RuleEvaluator.currentPlayer + " wins!!");
        System.exit(0);
    }

}
