package ch.uzh.group38;

public class RuleEvaluator {

    /*
    variable to keep track of whose turn it is
    */
    private static Board board;
    private static int currentPlayer;
    private static int lastX = -1;
    private static int lastY = -1;

    public static void update(Board CurrentBoard)
    {
       board = CurrentBoard;
    }

    //sets first Player to 1
    public static void resetCurrentPlayer(){
        currentPlayer = 1;
    }

    public static int getCurrentPlayer(){
        return currentPlayer;
    }

    public static void updateTurn(){
        //PLayer 1 is red, Player 2 is white
        if (currentPlayer == 1){
            currentPlayer = 2;
        }
        else{
            currentPlayer = 1;
        }
        lastX = -1;
        lastY = -1;
    }

    /*
    checks if the piece can be accessed by current player
     */
    public static boolean checkInput(int x, int y) {
        if (currentPlayer == 1) {
            return board.getField(x, y).isRed();
        }
        else if (currentPlayer == 2) {
            return board.getField(x, y).isWhite();
        }
        return false;
    }

    /*
    checks if input is a valid move
    */
    public static boolean checkValidity(Move move){

        int x1 = move.FromX();
        int y1 = move.FromY();
        int x2 = move.ToX();

        //check if move is going in the right direction
        //red pawns can only move negative y
        if (board.getField(x1,y1).isRed() && !board.getField(x1,y1).isKing()
            && x2 - x1 <= 0 ){
            //GUI.setMessage("This move is not valid");
                return false;
            }

        //white pawns can only move positive y
        if (board.getField(x1,y1).isWhite() && !board.getField(x1,y1).isKing()
            && x2 - x1 >= 0 ){
            //GUI.setMessage("This move is not valid");
                return false;
            }

        //check if it is players color  
        if (((board.getField(x1,y1).isRed() && currentPlayer == 1) || (board.getField(x1,y1).isWhite() && currentPlayer == 2))){            
            if (isJumpMove(move) || isSimpleMove(move)){
                
                if ((lastX != -1 && lastY != -1) && (x1 != lastX && y1 != lastY)){
                    //GUI.setMessage("Continue your move with same piece");
                    return false;
                }

                    //check if a jump move was possible
                if (isSimpleMove(move)){
                    for (int i = 0; i < 8; i++){
                        for (int j = 0; j < 8; j++){
                            if (currentPlayer == 1 && board.getField(i,j).isRed() || currentPlayer == 2 && board.getField(i,j).isWhite()){
                                if (checkForJumpMoves(i, j)){
                                    //GUI.setMessage("There is a possible jump move");
                                    return false;
                                }
                            }
                        }
                    }   
                }
            return true;
            }
        }
        // GUI.setMessage("This move is not valid");
        return false;
    }

    /*
    checks if there are possible jumpMoves from the current position
    */
    public static boolean checkForJumpMoves(int x, int y){

        if (board.getField(x, y).isKing() || board.getField(x, y).isRed()){
            if (x+2 < 8 && y+2 < 8){
                Move potentialMove = new Move(x, y, x+2, y+2);
                if (isJumpMove(potentialMove)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isWhite()){
            if (x-2 >= 0 && y+2 < 8) {
                Move potentialMove = new Move(x, y, x-2, y+2);
                if (isJumpMove(potentialMove)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isWhite()){
            if (x-2 >= 0 && y-2 >= 0){
                Move potentialMove = new Move(x, y, x-2, y-2);
                if (isJumpMove(potentialMove)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isRed()){
            if (x+2 < 8 && y-2 >= 0){
                Move potentialMove = new Move(x, y, x+2, y-2);
                return isJumpMove(potentialMove);
            }
        }

        return false;
    }

    private static boolean checkForSimpleMoves(int x, int y){

        if (board.getField(x, y).isKing() || board.getField(x, y).isRed()){
            if (x+1 < 8 && y+1 < 8) {
                Move potentialMove = new Move(x, y, x+1, y+1);
                if (isSimpleMove(potentialMove)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isWhite()){
            if (x-1 >= 0 && y+1 < 8){
                Move potentialMove = new Move(x, y, x-1, y+1);
                if (isSimpleMove(potentialMove)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isWhite()){
            if (x-1 >= 0 && y-1 >= 0){
                Move potentialMove = new Move(x, y, x-1, y-1);
                if (isSimpleMove(potentialMove)) {
                    return true;
                }
            }
        }

        if (board.getField(x, y).isKing() || board.getField(x, y).isRed()){
            if (x+1 < 8 && y-1 >= 0){
                Move potentialMove = new Move(x, y, x+1, y-1);
                return isSimpleMove(potentialMove);
            }
        }

        return false;
    }

    private static boolean isJumpMove(Move move){

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

    private static boolean isSimpleMove(Move move){

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
    public static boolean checkWinner(Board board){
        //check if there are pins left and if they can move
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                //if (currentPlayer == 2 && board.getField(i,j).isWhite() || (currentPlayer == 1 && board.getField(i,j).isRed())){
                    if (board.getField(i,j).isAnyMovePossible()){
                        return false;
                    }
                }
            }
        updateTurn();
        return true;        
    }




    public static void storeLastMove(Move move){
            lastX = move.ToX();
            lastY = move.ToY();

    }
}
