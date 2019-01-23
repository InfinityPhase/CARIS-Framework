package caris.framework.handlers;

import caris.framework.basehandlers.GeneralHandler;
import caris.framework.basereactions.Reaction;
import caris.framework.calibration.Constants;
import caris.framework.reactions.TrackGuildReaction;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;

public class TrackGuildHandler extends GeneralHandler {

	public TrackGuildHandler() {
		super("TrackGuild", false);
	}
	
	@Override
	protected boolean isTriggered(Event event) {
		return event instanceof GuildCreateEvent;
	}
	
	@Override
	protected Reaction process(Event event) {
		GuildCreateEvent guildCreateEvent = (GuildCreateEvent) event;
		return new TrackGuildReaction(guildCreateEvent.getGuild(), -1);
	}

	@Override
	public String getDescription() {
		return "Handles the creation of guilds on " + Constants.NAME + "'s servers.";
	}
	
}