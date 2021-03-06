package polytech.projetrevamuseum;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import polytech.projetrevamuseum.activities.Main2Activity;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;


public class Obj3DView extends RendererActivity {
    String rawPath;
    private String identity;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        Intent intent = getIntent();
        identity = intent.getStringExtra(Main2Activity.EXTRA_MESSAGE);
        rawPath = extras.getString("raw_path");
    }

    private Object3dContainer faceObject3D;
    float touchedX = 0;
    float touchedY = 0;
    float xAngle= 0;
    float myscale=0.7f;
    float yAngle=0;
    private ScaleGestureDetector myScaler;
    @Override
    public void initScene()//Piece currentPiece
    {
        //scene.lights().add(new Light());
        // scene.lights().add(new Light());

        Light myLight = new Light();
        myLight.diffuse.setAll(242,208,164,255);
        myLight.ambient.setAll(242,208,164,90);
        myLight.specular.setAll(164,48,63,255);
        myLight.emissive.setAll(164,48,63,255);
        myLight.position.setZ(400);
        myLight.position.setY(400);


        scene.lights().add(myLight);
        Parser.sdcard=true;
        IParser myParser = Parser.createParser(Parser.Type.OBJ, identity,true);

        myParser.parse();

        faceObject3D = myParser.getParsedObject();
        faceObject3D.position().x = faceObject3D.position().y = faceObject3D.position().z = 0;
        faceObject3D.scale().x = faceObject3D.scale().y = faceObject3D.scale().z = 0.7f;
        //    faceObject3D.scale().x = faceObject3D.scale().y = faceObject3D.scale().z = 1.0f;

        scene.addChild(faceObject3D);


    }
    public boolean onTouchEvent(MotionEvent event)
    {
        if(myScaler!=null)
            myScaler.onTouchEvent(event);
        else{
            myScaler= new ScaleGestureDetector(this, new ScaleListener());
            myScaler.onTouchEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            touchedX = event.getX();
            touchedY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            xAngle += (touchedX - event.getX())/3f;
            yAngle += (touchedY - event.getY())/3f;

            touchedX = event.getX();
            touchedY = event.getY();
        }
        return true;

    }
    @Override
    public void updateScene() {
        faceObject3D.scale().x = faceObject3D.scale().y = faceObject3D.scale().z = myscale;
        faceObject3D.rotation().x = -yAngle;
        faceObject3D.rotation().z = -xAngle;
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            myscale *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            myscale = Math.max(0.01f, Math.min(myscale, 5.0f));

            //invalidate();
            return true;
        }
    }
}