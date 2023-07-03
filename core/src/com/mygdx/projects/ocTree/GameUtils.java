package com.mygdx.projects.ocTree;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;


// Creates collision shape from object model.
public class GameUtils {

    public static void cloneTransformationTo(ModelInstance modelSrc, ModelInstance modelDest) {
        cloneTransformationTo(modelSrc.transform, modelDest.transform);
        modelDest.calculateTransforms();
    }

    public static void cloneTransformationTo(Matrix4 mSrc, Matrix4 mDest) {
        Quaternion mSrcRot = new Quaternion();
        Vector3 mSrcTrans = new Vector3();
        mSrc.getTranslation(mSrcTrans);
        mSrc.getRotation(mSrcRot);
        mDest.setToTranslation(mSrcTrans);
        mDest.rotate(mSrcRot);
        mDest.scale(mSrc.getScaleX(), mSrc.getScaleY(), mSrc.getScaleZ());
    }

    // not tested
    public static void setTranslation(ModelInstance mi, Vector3 pos) {
        Quaternion mSrcRot = new Quaternion();
        mi.transform.getRotation(mSrcRot);
        mi.transform.setToTranslation(pos);
        mi.transform.rotate(mSrcRot);
        mi.calculateTransforms();
    }

    public static void addTranslation(ModelInstance mi, Vector3 pos) {
        Quaternion mSrcRot = new Quaternion();
        Vector3 mSrcTrans = new Vector3();
        mi.transform.getTranslation(mSrcTrans);
        mi.transform.getRotation(mSrcRot);
        mi.transform.setToTranslation(mSrcTrans.add(pos));
        mi.transform.rotate(mSrcRot);
        mi.calculateTransforms();
    }

    // why this was named add scale? shouldn't it be set Scale?
    public static void setScale(ModelInstance model, Vector3 scale) {
        Quaternion mSrcRot = new Quaternion();
        Vector3 mSrcTrans = new Vector3();
        model.transform.getTranslation(mSrcTrans);
        model.transform.getRotation(mSrcRot);
        model.transform.setToTranslation(mSrcTrans);
        model.transform.rotate(mSrcRot);
        model.transform.scale(scale.x, scale.y, scale.z);
    }

    public static void addRotation(ModelInstance model, Vector3 axis, float degrees) {
        Quaternion mSrcRot = new Quaternion();
        Vector3 mSrcTrans = new Vector3();
        model.transform.getTranslation(mSrcTrans);
        model.transform.getRotation(mSrcRot);
        model.transform.setToTranslation(mSrcTrans);
        model.transform.rotate(mSrcRot);
        model.transform.rotate(axis, degrees);
        model.transform.scale(model.transform.getScaleX(), model.transform.getScaleY(), model.transform.getScaleZ());
    }


}
