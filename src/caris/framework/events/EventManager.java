package caris.framework.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import caris.framework.basehandlers.Handler;
import caris.framework.basereactions.MultiReaction;
import caris.framework.basereactions.NullReaction;
import caris.framework.basereactions.Reaction;
import caris.framework.main.Brain;
import caris.framework.reactions.SaveDataReaction;
import caris.framework.utilities.Logger;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;

public class EventManager extends SuperEvent {
	
	@EventSubscriber
	@Override
	public void onEvent(Event event) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				List<Handler> handlerList = new ArrayList<Handler>();
				handlerList.addAll(Brain.handlers.values());
				handlerList.addAll(Brain.interactives);
				List<Reaction> reactions = new ArrayList<Reaction>();
				List<Reaction> passiveQueue = new ArrayList<Reaction>();
				for( Handler h : handlerList ) {
					Reaction r = h.handle(event);
					if( r != null && !(r instanceof NullReaction) ) {
						if( r.priority == -1 ) {
							passiveQueue.add(r);
						} else {
							reactions.add(r);
						}
					}
				}
				while( !Brain.cli.isReady() || !Brain.cli.isLoggedIn() ) {
					try {
						Logger.error("Client disconnected. Waiting for reconnect.");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if( !reactions.isEmpty() ) {
					Reaction[] options = new Reaction[reactions.size()];
					for( int f=0; f<reactions.size(); f++ ) {
						options[f] = reactions.get(f);
					}
					Arrays.sort(options);
					options[0].run();
				}
				if( !passiveQueue.isEmpty() || !reactions.isEmpty() ) {
					passiveQueue.add(new SaveDataReaction());
				}
				MultiReaction passiveQueueExecutor = new MultiReaction(passiveQueue);
				passiveQueueExecutor.start();
			}
		};
		Brain.threadQueue.add(thread);
	}
}
