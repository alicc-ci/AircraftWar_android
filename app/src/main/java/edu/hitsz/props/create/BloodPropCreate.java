package edu.hitsz.props.create;

import edu.hitsz.props.BaseProp;
import edu.hitsz.props.BloodProp;

public class BloodPropCreate implements PropFactory {
    @Override
    public BaseProp createProp(int x,int y) {
        return new BloodProp(x, y);
    }
}
