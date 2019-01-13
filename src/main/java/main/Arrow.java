package main;

import java.util.HashSet;
import java.util.Set;

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

    public static Set<Arrow> getDefaultOptions(){
        Set<Arrow> options = new HashSet<>();
        options.add(Arrow.UP);
        options.add(Arrow.DOWN);
        options.add(Arrow.LEFT);
        options.add(Arrow.RIGHT);
        return options;
    }
}
