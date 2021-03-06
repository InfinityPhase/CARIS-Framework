package caris.framework.reactions;

import caris.framework.basehandlers.InteractiveHandler;
import caris.framework.basereactions.Reaction;
import caris.framework.utilities.BotUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

public class InteractiveCreateReaction extends Reaction {

	public IChannel channel;
	public InteractiveHandler interactive;
	
	public InteractiveCreateReaction(IChannel channel, InteractiveHandler interactive) {
		this(channel, interactive, -1);
	}
	
	public InteractiveCreateReaction(IChannel channel, InteractiveHandler interactive, int priority) {
		super(1);
		this.channel = channel;
		this.interactive = interactive;
	}
	
	@Override
	public void process() {
		IMessage source = null;
		if( !interactive.getDefault().content.isEmpty() && interactive.getDefault().embed != null ) {
			source = BotUtils.sendMessage(channel, interactive.getDefault().content, interactive.getDefault().embed);
		} else if( interactive.getDefault().content.isEmpty() && interactive.getDefault().embed == null ) {
			source = BotUtils.sendMessage(channel, "```http\nLoading Interactive...\n```");
		} else if( interactive.getDefault().content.isEmpty() ) {
			source = BotUtils.sendMessage(channel, interactive.getDefault().embed);
		} else {
			source = BotUtils.sendMessage(channel, interactive.getDefault().content);
		}
		interactive.create(source).start();
	}
	
}
