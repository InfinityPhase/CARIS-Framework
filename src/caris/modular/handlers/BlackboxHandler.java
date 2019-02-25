package caris.modular.handlers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import caris.framework.basehandlers.MessageHandler;
import caris.framework.basereactions.MultiReaction;
import caris.framework.basereactions.Reaction;
import caris.framework.calibration.Constants;
import caris.framework.calibration.Constants.Access;
import caris.framework.calibration.Keywords;
import caris.framework.embedbuilders.ErrorBuilder;
import caris.framework.embedbuilders.ErrorBuilder.ErrorType;
import caris.framework.events.MessageEventWrapper;
import caris.framework.main.Brain;
import caris.framework.reactions.MessageReaction;
import caris.framework.reactions.UpdateChannelReaction;
import caris.modular.reactions.BlackboxPurgeReaction;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

public class BlackboxHandler extends MessageHandler {
	
	public BlackboxHandler() {
		super("Blackbox", Access.ADMIN);
	}

	@Override
	public Reaction onStartup() {
		MultiReaction reconnectBlackbox = new MultiReaction(-1);
		for( IGuild guild : Brain.cli.getGuilds() ) {
			for( IChannel channel : guild.getChannels() ) {
				if( Brain.variables.getChannelInfo(channel).channelData.has("blackbox") ) {
					if( Brain.variables.getChannelInfo(channel).channelData.get("blackbox") instanceof JSONArray ) {
						List<Long> messageList = new ArrayList<Long>();
						for( int f=0; f<((JSONArray) Brain.variables.getChannelInfo(channel).channelData.get("blackbox")).length(); f++ ) {
							messageList.add((Long) ((JSONArray) Brain.variables.getChannelInfo(channel).channelData.get("blackbox")).get(f));
						}
						reconnectBlackbox.add(new UpdateChannelReaction(channel, "blackbox", messageList, true));
					}
				}
			}
		}
		return reconnectBlackbox;
	}
	
	@Override
	protected boolean isTriggered(MessageEventWrapper messageEventWrapper) {
		return mentioned(messageEventWrapper) && messageEventWrapper.containsAnyPhrases("blackbox", "black box") && (messageEventWrapper.containsAnyWords(Keywords.POSITIVE) || messageEventWrapper.containsAnyWords(Keywords.NEGATIVE) || messageEventWrapper.containsAnyWords(Keywords.CANCEL));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Reaction process(MessageEventWrapper messageEventWrapper) {
		MultiReaction blackbox = new MultiReaction(1);
		if( messageEventWrapper.containsAnyWords(Keywords.POSITIVE) ) {
			if( !Brain.variables.getChannelInfo(messageEventWrapper.getMessage()).channelData.has("blackbox") ) {
				blackbox.add(new UpdateChannelReaction(messageEventWrapper.getChannel(), "blackbox", new ArrayList<Long>(), true));
				blackbox.add(new MessageReaction(messageEventWrapper.getChannel(), "Blackbox opened!"));
			} else {
				blackbox.add(new MessageReaction(messageEventWrapper.getChannel(), ErrorBuilder.getErrorEmbed(ErrorType.USAGE, "A blackbox is already open in this channel!")));
			}
		} else if( messageEventWrapper.containsAnyWords(Keywords.NEGATIVE) ) {
			if( Brain.variables.getChannelInfo(messageEventWrapper.getMessage()).channelData.has("blackbox") ) {
				blackbox.add(new BlackboxPurgeReaction(messageEventWrapper.getChannel(), ((List<Long>) Brain.variables.getChannelInfo(messageEventWrapper.getMessage()).channelData.get("blackbox"))));
				blackbox.add(new UpdateChannelReaction(messageEventWrapper.getChannel(), "blackbox", null, true));
				blackbox.add(new MessageReaction(messageEventWrapper.getChannel(), "Blackbox closed!"));
			} else {
				blackbox.add(new MessageReaction(messageEventWrapper.getChannel(), ErrorBuilder.getErrorEmbed(ErrorType.USAGE, "No blackbox open in this channel!")));
			}
		} else if( messageEventWrapper.containsAnyWords(Keywords.CANCEL) ) {
			if( Brain.variables.getChannelInfo(messageEventWrapper.getMessage()).channelData.has("blackbox") ) {
				blackbox.add(new UpdateChannelReaction(messageEventWrapper.getChannel(), "blackbox", null, true));
				blackbox.add(new MessageReaction(messageEventWrapper.getChannel(), "Blackbox cancelled!"));
			} else {
				blackbox.add(new MessageReaction(messageEventWrapper.getChannel(), ErrorBuilder.getErrorEmbed(ErrorType.USAGE, "No blackbox open in this channel!")));
			}
		}
		return blackbox;
	}

	@Override
	public String getDescription() {
		return "Open a blackbox, then close it later to delete all messages sent after it was opened.";
	}
	
	@Override
	public List<String> getUsage() {
		List<String> usage = new ArrayList<String>();
		usage.add(Constants.NAME + ", open up a blackbox");
		usage.add(Constants.NAME + ", close the blackbox");
		usage.add(Constants.NAME + ", cancel the current blackbox");
		return usage;
	}
	
}
