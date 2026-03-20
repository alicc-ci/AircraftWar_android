package edu.hitsz.props.create;

import edu.hitsz.props.BaseProp;
import edu.hitsz.props.BulletProp;

public class BulletPropCreate implements PropFactory {
    @Override
    public BaseProp createProp(int x,int y) {
        return new BulletProp(x, y);
    }
}
