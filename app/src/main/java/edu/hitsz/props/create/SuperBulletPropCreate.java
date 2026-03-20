package edu.hitsz.props.create;

import edu.hitsz.props.BaseProp;
import edu.hitsz.props.SuperBulletProp;

public class SuperBulletPropCreate implements PropFactory {
    @Override
    public BaseProp createProp(int x,int y) {
        return new SuperBulletProp(x, y);
    }
}
