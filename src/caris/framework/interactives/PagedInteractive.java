package caris.framework.interactives;

import caris.framework.basehandlers.InteractiveHandler;
import caris.framework.basereactions.MultiReaction;
import caris.framework.basereactions.Reaction;
import caris.framework.calibration.EmojiSet;
import caris.framework.reactions.MessageEditReaction;
import caris.framework.reactions.ReactAddReaction;
import caris.framework.reactions.ReactRemoveReaction;
import caris.framework.tokens.MessageContent;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;

public class PagedInteractive extends InteractiveHandler {

	public int page;
	public EmbedObject[] pages;
	
	public PagedInteractive(EmbedObject...pages) {
		super("Paged");
		page = 0;
		this.pages = pages;
	}

	@Override
	public Reaction process(ReactionEvent reactionEvent) {
		MultiReaction interaction = new MultiReaction(-1);
		if( equivalentEmojis(reactionEvent.getReaction(), EmojiSet.LEFT ) ) {
			page -= 1;
			while( page < 0 ) {
				page += pages.length;
			}
		} else if( equivalentEmojis(reactionEvent.getReaction(), EmojiSet.RIGHT) ) {
			page += 1;
			while( page >= pages.length ) {
				page -= pages.length;
			}
		}
		interaction.add(new ReactRemoveReaction(source, reactionEvent.getUser(), reactionEvent.getReaction()));
		interaction.add(new MessageEditReaction(source, new MessageContent(pages[page])));
		return interaction;
	}

	@Override
	public MessageContent getDefault() {
		return new MessageContent("", pages[0]);
	}
	
	@Override
	public String getDescription() {
		return "An interactive that can be paged through";
	}

	@Override
	protected Reaction open() {
		MultiReaction addEmojis = new MultiReaction(-1);
		addEmojis.add(new ReactAddReaction(source, EmojiSet.LEFT));
		addEmojis.add(new ReactAddReaction(source, EmojiSet.RIGHT));
		return addEmojis;
	}

	@Override
	protected Reaction close() {
		return null;
	}
	
}
