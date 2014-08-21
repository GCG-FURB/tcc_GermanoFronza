package br.furb.inf.tcc.util.scene;

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jmex.font2d.Text2D;
import com.jmex.font2d.Font2D;

/**
 * This class has been extracted from a web forum..
 * @author Unknown author.
 */
public class Text extends Node {
	
	private static final long serialVersionUID = 1L;
	
	public final static int HA_LEFT = 0;
	public final static int HA_CENTER = 1;
	public final static int HA_RIGHT = 2;
	public final static int VA_TOP = 0;
	public final static int VA_MIDDLE = 1;
	public final static int VA_BOTTOM = 2;

	private DisplaySystem display;
	private Node target;
	private Text2D text2D;
	private Vector3f offset;
	private Vector3f screenCoordinates;
	private Vector2f halfDisplay;
	private float alignOffset;
	private float valignOffset;
	private boolean enabled;
	private ColorRGBA color;

	/**
	 * Class constructor.
	 *
	 * @param target The target node at which to display text.
	 * @param offset The offset from the target to display text.
	 * @param text The text to display to screen.
	 */
	public Text(Node target,
			Vector3f offset,
			String text) {

		super(target.getName() + " Text Node");
		
		this.target = target;
		this.offset = offset;
		
		display = DisplaySystem.getDisplaySystem();
		enabled = true;
		halfDisplay = new Vector2f((display.getWidth() / 2), (display.getHeight() / 2));
			
		Font2D font2D = new Font2D();
		text2D = font2D.createText(text, 10, 0);
		text2D.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		
		color = text2D.getTextColor();
	
		setModelBound(new BoundingBox());
		updateModelBound();
		attachChild(text2D);
	}
	
	/**
	 * True if the text is to be displayed during rendering.
	 *
	 * @return True if the text should be displayed.
	 */
	public boolean isEnabled() {
		
		return enabled;
	}
	
	/**
	 * Sets whether we should cull the text during rendering.
	 *
	 * @param enabled True if the text should be displayed.
	 */
	public void setEnabled(boolean enabled) {
		
		this.enabled = enabled;
		
		if(enabled) {
			setCullMode(CULL_DYNAMIC);
		} else {
			setCullMode(CULL_ALWAYS);
		}
	}
	
	/**
	 * Sets the colour of the text.
	 *
	 * @param color The colour of the text.
	 */
	public void setColor(ColorRGBA color) {
		
		this.color = color;
		
		text2D.setTextColor(color);
	}
	
	/**
	 * Sets the offset from the target to display text at.
	 *
	 * @param offset The offset from the target to display text.
	 */
	public void setOffset(Vector3f offset) {
		
		this.offset = offset;
	}
	
	/**
	 * Sets the text to display.
	 *
	 * @param text The text string to display.
	 */
	public void setText(String text) {
		
		text2D.setText(text);
	}
	
	/**
	 * Sets the scale of this text node.
	 *
	 * @param scale The desired scale of this text node.
	 */
	public void setLocalScale(float scale) {
		
		super.setLocalScale(scale);
		
		alignOffset = alignOffset * scale;
		valignOffset = valignOffset * scale;
	}
	
	/**
	 * Sets the scale of this text node.
	 *
	 * @param scale The desired scale of this text node.
	 */
	public void setLocalScale(Vector3f scale) {
		
		super.setLocalScale(scale);
		
		alignOffset = alignOffset * scale.x;
		valignOffset = valignOffset * scale.y;
	}
	
	/**
	 * Sets the horizontal alignment of text to the target.
	 *
	 * @param align The horizontal alignment of text.
	 */
	public void setHorizontalAlignment(int align) {
		
		if(align == HA_LEFT) {
			alignOffset = 0;
		} else if(align == HA_CENTER) {
			alignOffset = (text2D.getWidth() / 2) * -1;
		} else if(align == HA_RIGHT) {
			alignOffset = text2D.getWidth() * -1;
		}
		
		alignOffset = alignOffset * getLocalScale().x;
	}
	
	/**
	 * Sets the vertical alignment of text to the target.
	 *
	 * @param valign The vertical alignment of text.
	 */
	public void setVerticalAlignment(int valign) {
		
		if(valign == VA_TOP) {
			valignOffset = 0;
		} else if(valign == VA_MIDDLE) {
			valignOffset = (text2D.getHeight() / 2) * -1;
		} else if(valign == VA_BOTTOM) {
			valignOffset = text2D.getHeight() * -1;	
		}		
		
		valignOffset = valignOffset * getLocalScale().y;
	}
	
	/**
	 * Updates this nodes world transform data.
	 *
	 * @param tpf The time elapsed since the last update.
	 */
	public void updateWorldData(float tpf) {
		
		if(enabled) {
			screenCoordinates = display.getScreenCoordinates(target.getWorldTranslation().add(offset));

			getLocalTranslation().x = screenCoordinates.x + alignOffset;
			getLocalTranslation().y = screenCoordinates.y + valignOffset;
			
			// determine if any text is on screen for processing and establish alpha magnitude via pythagoras
			if((getLocalTranslation().x < display.getWidth()) && ((getLocalTranslation().x - alignOffset) > 0) && (getLocalTranslation().y < display.getHeight()) && ((getLocalTranslation().y - valignOffset) > 0)) {
				screenCoordinates = display.getScreenCoordinates(target.getWorldTranslation());
				color.a = 1 - (FastMath.sqrt(FastMath.sqr(screenCoordinates.x - halfDisplay.x) + FastMath.sqr(screenCoordinates.y - halfDisplay.y)) / ((halfDisplay.y + halfDisplay.x) / 2));
			}
		}
		
		super.updateWorldData(tpf);
	}		
}