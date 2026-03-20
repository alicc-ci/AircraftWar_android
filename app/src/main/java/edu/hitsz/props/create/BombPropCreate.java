package edu.hitsz.props.create;

import edu.hitsz.props.BaseProp;
import edu.hitsz.props.BombProp;

public class BombPropCreate implements PropFactory {
    @Override
    public BaseProp createProp(int x, int y) {
        return new BombProp(x, y);
    }
}
