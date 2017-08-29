package eu.dzim.shared.fx.ui;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * SimpleDialog events, used exclusively by the following methods:
 * <ul>
 * <li>{@link SimpleDialog#close()}
 * <li>{@link SimpleDialog#getShowAnimation()}
 * </ul>
 */
public class SimpleDialogEvent extends Event {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Construct a new SimpleDialogEvent {@code Event} with the specified event type
	 * 
	 * @param eventType
	 *            the event type
	 */
	public SimpleDialogEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
	
	/**
	 * Construct a new SimpleDialogEvent {@code Event} with the specified event type and data
	 * 
	 * @param source
	 *            the source of the event (e.g. the {@link SimpleDialog} instance)
	 * @param target
	 *            the target of the event (e.g. the {@link SimpleDialog}'s content container)
	 * @param eventType
	 *            the event type
	 */
	public SimpleDialogEvent(Object source, EventTarget target, EventType<? extends Event> eventType) {
		super(source, target, eventType);
	}
	
	/**
	 * This event occurs when a SimpleDialog is closed, no longer visible to the user (after the exit animation ends )
	 */
	public static final EventType<SimpleDialogEvent> CLOSED = new EventType<SimpleDialogEvent>(Event.ANY, "DIALOG_CLOSED");
	
	/**
	 * This event occurs when a SimpleDialog is opened, visible to the user (after the entrance animation ends )
	 */
	public static final EventType<SimpleDialogEvent> OPENED = new EventType<SimpleDialogEvent>(Event.ANY, "DIALOG_OPENED");
	
	/**
	 * This event occurs when a SimpleDialog is about to be shown. Can be used to apply some effects, etc.
	 */
	public static final EventType<SimpleDialogEvent> BEFORE_SHOW = new EventType<SimpleDialogEvent>(Event.ANY, "DIALOG_BEFORE_SHOW");
	
}