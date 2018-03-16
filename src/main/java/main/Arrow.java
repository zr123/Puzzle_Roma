package main;

public enum Arrow{
    NONE, UP, DOWN, LEFT, RIGHT, CIRCLE;

    public String toChar(){
        switch(this){
            case UP:        return "⬆";
            case LEFT:      return "⬅";
            case RIGHT:     return "➡";
            case DOWN:      return "⬇";
            case NONE:      return " ";
            case CIRCLE:    return "O";
        }
        return "";
    }
}
