package caris.framework.basehandlers;

import java.util.List;

import caris.framework.basereactions.Reaction;
import caris.framework.calibration.Constants;
import caris.framework.events.MessageEventWrapper;
import caris.framework.main.Brain;
import caris.framework.tokens.RedirectedMessage;
import caris.framework.utilities.Logger;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public abstract class MessageHandler extends Handler {
		
	public enum Access {
		DEFAULT {
			@Override
			public String toString() {
				return "Default";
			}
		},
		ADMIN {
			@Override
			public String toString() {
				return "Admin";
			}
		},
		DEVELOPER {
			@Override
			public String toString() {
				return "Developer";
			}
		},
		PASSIVE  {
			@Override
			public String toString() {
				return "Passive";
			}
		},
	};
	
	public String invocation;
	public Access accessLevel;
		
	public MessageHandler(String name) {
		this(name, false, Access.DEFAULT);
	}
	
	public MessageHandler(String name, boolean allowBots) {
		this(name, allowBots, Access.DEFAULT);
	}
	
	public MessageHandler(String name, Access accessLevel) {
		this(name, false, Access.DEFAULT);
	}
	
	public MessageHandler(String name, boolean allowBots, Access accessLevel) {
		super(name, allowBots);
		this.accessLevel = accessLevel;
		this.invocation = Constants.INVOCATION_PREFIX + name;
	}
	
	@Override
	public Reaction handle(Event event) {
		Logger.debug("Checking " + name, 0, true);
		if( event instanceof MessageReceivedEvent ) {
			MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
			if( !messageReceivedEvent.getChannel().isPrivate() ) {
				MessageEventWrapper messageEventWrapper = wrap(messageReceivedEvent);
				if( botFilter(event) ) {
					Logger.debug("Event from a bot. Aborting.", 1, true);
					return null;
				} else if( isTriggered(messageEventWrapper) && accessGranted(messageEventWrapper) ) {
					Logger.debug("Conditions satisfied for " + name + ". Processing.", 1);
					Reaction result = process(messageEventWrapper);
					if( result == null ) {
						Logger.debug("No Reaction produced. Aborting.", 1, true);
					} else {
						Logger.debug("Reaction produced from " + name + ". Adding to queue." , 1);
					}
					return result;
				} else {
					Logger.debug("Conditions unsatisfied. Aborting.", 1, true);
					return null;
				}
			} else {
				Logger.debug("Message from a private channel. Aborting.", 1, true);
				return null;
			}
		} else {
			Logger.debug("Event not a MessageReceivedEvent. Aborting.", 1, true);
			return null;
		}
	}
	
	private MessageEventWrapper wrap(MessageReceivedEvent messageReceivedEvent) {
		MessageEventWrapper messageEventWrapper = new MessageEventWrapper(messageReceivedEvent);
		if( messageEventWrapper.tokens.size() > 0 ) {
			String token = messageEventWrapper.tokens.get(0);
			if( token.startsWith("{") && token.endsWith("}") && token.length() > 2 && messageEventWrapper.message.length() > token.length() + 1) {
				String tokenContent = token.substring(1, token.length()-1);
				try {
					Long channelID = Long.parseLong(tokenContent);
					for( Long guildID : Brain.variables.guildIndex.keySet() ) {
						for( Long id : Brain.variables.guildIndex.get(guildID).channelIndex.keySet() ) {
							if( id == channelID ) {
								messageEventWrapper = new MessageEventWrapper(
														new MessageReceivedEvent(
															new RedirectedMessage(messageReceivedEvent.getMessage(), Brain.cli.getGuildByID(guildID).getChannelByID(id), messageEventWrapper.message.substring(messageEventWrapper.message.indexOf("}"+2)))
														));
							}
						}
					}
				} catch (NumberFormatException e) {
					
				}
			}
		}
		return messageEventWrapper;
	}
	
	protected boolean mentioned(MessageEventWrapper messageEventWrapper) {
		return messageEventWrapper.containsWord(Constants.NAME);
	}
	
	protected boolean invoked(MessageEventWrapper messageEventWrapper) {
		return messageEventWrapper.tokens.size() > 0 ? messageEventWrapper.tokens.get(0).equalsIgnoreCase(invocation) : false;
	}
	
	public boolean accessGranted(MessageEventWrapper messageEventWrapper) {
		return (accessLevel != Access.ADMIN || messageEventWrapper.elevatedAuthor) && (accessLevel != Access.DEVELOPER || messageEventWrapper.developerAuthor);
	}
	
	public abstract List<String> getUsage();
	
	protected abstract boolean isTriggered(MessageEventWrapper messageEventWrapper);
	protected abstract Reaction process(MessageEventWrapper messageEventWrapper);
}
