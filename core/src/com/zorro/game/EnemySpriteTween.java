package com.zorro.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import aurelienribon.tweenengine.TweenAccessor;

public class EnemySpriteTween implements TweenAccessor<Sprite>{ 

	public static final int POSITION_X = 1; 
	public static final int POSITION_Y = 2; 
	public static final int POSITION_XY = 3;
	private Vector3 position;

    public EnemySpriteTween(Vector3 position) {
    	this.position = position;
	}

	@Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {
        switch(tweenType) {
        case POSITION_X: 
        	returnValues[0] = position.x;
        	return 1;
        	
        case POSITION_Y: 
        	returnValues[0] = position.y;
        	return 1;
        	
        case POSITION_XY: 
        	returnValues[0] = position.x; 
        	returnValues[1] = position.y; 
        	return 2;
        	
        default: assert false; return 0;
        }
    }

    @Override
    public void setValues(Sprite target, int tweenType, float[] newValues) {
        switch (tweenType) {
        case POSITION_X: 
        	position.x = newValues[0]; 
        	break;

        case POSITION_Y: 
        	position.y = newValues[0]; 
        	break;

        case POSITION_XY: 
        	position.x = newValues[0]; 
        	position.y = newValues[1]; 
        	break;

        default: assert false; break;

        }
    }
}