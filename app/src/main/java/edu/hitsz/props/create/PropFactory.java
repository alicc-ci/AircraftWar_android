package edu.hitsz.props.create;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.props.BaseProp;

public interface PropFactory {
    BaseProp createProp(int x, int y);
}
