package caris.framework.reactions;

import com.vdurmont.emoji.Emoji;

import caris.framework.basereactions.Reaction;
import caris.framework.calibration.Constants;
import caris.framework.utilities.Logger;
import sx.blah.discord.handle.obj.IMessage;

public class ReactAddReaction extends Reaction {
	
	public IMessage message;
	public Emoji[] emojis;
	
	public ReactAddReaction(IMessage message, Emoji...emojis) {
		this(message, emojis, -1);
	}
	
	public ReactAddReaction(IMessage message, Emoji emoji, int priority) {
		super(priority);
		this.message = message;
		this.emojis = new Emoji[] {emoji};
	}
	
	public ReactAddReaction(IMessage message, Emoji[] emojis, int priority) {
		super(priority);
		this.message = message;
		this.emojis = emojis;
	}
	
	@Override
	public void process() {
		for( Emoji emoji : emojis ) {
			message.addReaction(emoji);
			try {
				Thread.sleep(Constants.REACTION_EXECUTE_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Logger.print("Reaction [" + emoji.getAliases().toString() + "] added to message (" + message.getLongID() + ")", 3);
		}
	}
	
}
