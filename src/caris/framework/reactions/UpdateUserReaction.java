package caris.framework.reactions;

import caris.framework.basereactions.Reaction;
import caris.framework.main.Brain;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class UpdateUserReaction extends Reaction {
	public IGuild guild;
	public IUser user;
	public String key;
	public Object value;
	public boolean override;
	
	public UpdateUserReaction(IGuild guild, IUser user, String key, Object value) {
		this(guild, user, key, value, false, -1);
	}
	
	public UpdateUserReaction(IGuild guild, IUser user, String key, Object value, boolean override) {
		this(guild, user, key, value, override, -1);
	}
	
	public UpdateUserReaction(IGuild guild, IUser user, String key, Object value, int priority) {
		this(guild, user, key, value, false, priority);
	}
	
	public UpdateUserReaction(IGuild guild, IUser user, String key, Object value, boolean override, int priority) {
		super(priority);
		this.guild = guild;
		this.user = user;
		this.key = key;
		this.value = value;
		this.override = override;
	}
	
	@Override
	public void run() {
		if( override || !Brain.variables.guildIndex.get(guild.getLongID()).userIndex.get(user.getLongID()).userData.has(key) ) {
			Brain.variables.guildIndex.get(guild.getLongID()).userIndex.get(user.getLongID()).userData.put(key, value);
			}
	}
}
