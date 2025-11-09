package gameLogic;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class Scene extends BaseAppState {

    private Node humanoid;
    private static final float ENV_TIME = 0.5f;
    private float timer=0.0f;

    @Override
    protected void initialize(Application app) {
        humanoid = (Node) app.getAssetManager().loadModel("assets/Models/skeleton.glb");

        humanoid.setLocalScale(1f);
        humanoid.setName("Humanoid");

        ChaseCamera chaseCamera = new ChaseCamera(app.getCamera(), humanoid.getChild("rt-knee-joint"), app.getInputManager());
        chaseCamera.setDragToRotate(true);
        chaseCamera.setSmoothMotion(true);
        chaseCamera.setDefaultDistance(-5f);
        chaseCamera.setMaxDistance(-5f);
        chaseCamera.setMinDistance(-5f);
        chaseCamera.setDefaultVerticalRotation(-FastMath.QUARTER_PI/2);
        chaseCamera.setDefaultHorizontalRotation(-FastMath.HALF_PI);
        chaseCamera.setHideCursorOnRotate(true);
        ((SimpleApplication)getApplication()).getRootNode().attachChild(humanoid);

        CollisionShape shape = CollisionShapeFactory.createDynamicMeshShape(humanoid.getChild("rt-tibia"));
        float mass = 0.1f;
        RigidBodyControl legRbc = new RigidBodyControl(shape, mass);
        humanoid.getChild("rt-tibia").addControl(legRbc);
        PhysicsSpace physicsSpace = app.getStateManager()
                .getState(BulletAppState.class).getPhysicsSpace();
        legRbc.setPhysicsSpace(physicsSpace);
        legRbc.setEnableSleep(false);
        physicsSpace.add(legRbc);

//        legRbc.applyForce(new Vector3f(0f,1f,0f), humanoid.getChild("rt-tibia").getLocalScale().divide(2f));
//
//        legRbc.applyForce(new Vector3f(
//                        Vector3f.UNIT_Y.x * FastMath.cos(FastMath.PI/2f),
//                        Vector3f.UNIT_Y.y * FastMath.sin(FastMath.PI/2f),
//                        Vector3f.UNIT_Y.z
//                )
//        , humanoid.getChild("rt-tibia").getLocalScale().divide(2f));
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public void update(float tpf) {
        //synchronized component addition to minimize scene tear
        timer+=tpf;
        if ( timer >= ENV_TIME ){
            if(!getStateManager().hasState(getStateManager().getState(Environment.class))){
                getApplication().getStateManager().attach(new Environment(humanoid));
            }
            getApplication().getStateManager().detach(this);
        }
    }
}
