package com.nisovin.shopkeepers.commands.lib.arguments;

import java.util.Collections;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.commands.lib.ArgumentFilter;
import com.nisovin.shopkeepers.commands.lib.ArgumentParseException;
import com.nisovin.shopkeepers.commands.lib.CommandContextView;
import com.nisovin.shopkeepers.commands.lib.CommandInput;
import com.nisovin.shopkeepers.lang.Messages;
import com.nisovin.shopkeepers.text.Text;

/**
 * Determines an online player by the given UUID input.
 */
public class PlayerByUUIDArgument extends ObjectByIdArgument<UUID, Player> {

	public PlayerByUUIDArgument(String name) {
		this(name, ArgumentFilter.acceptAny());
	}

	public PlayerByUUIDArgument(String name, ArgumentFilter<Player> filter) {
		this(name, filter, PlayerUUIDArgument.DEFAULT_MINIMUM_COMPLETION_INPUT);
	}

	public PlayerByUUIDArgument(String name, ArgumentFilter<Player> filter, int minimumCompletionInput) {
		super(name, filter, new IdArgumentArgs(minimumCompletionInput));
	}

	@Override
	protected ObjectIdArgument<UUID> createIdArgument(String name, IdArgumentArgs args) {
		return new PlayerUUIDArgument(name, ArgumentFilter.acceptAny(), args.minimumCompletionInput) {
			@Override
			protected Iterable<UUID> getCompletionSuggestions(CommandInput input, CommandContextView context, String idPrefix) {
				return PlayerByUUIDArgument.this.getCompletionSuggestions(input, context, minimumCompletionInput, idPrefix);
			}
		};
	}

	@Override
	public Text getInvalidArgumentErrorMsg(String argumentInput) {
		if (argumentInput == null) argumentInput = "";
		Text text = Messages.commandPlayerArgumentInvalid;
		text.setPlaceholderArguments(this.getDefaultErrorMsgArgs());
		text.setPlaceholderArguments(Collections.singletonMap("argument", argumentInput));
		return text;
	}

	@Override
	protected Player getObject(CommandInput input, CommandContextView context, UUID uuid) throws ArgumentParseException {
		return Bukkit.getPlayer(uuid);
	}

	@Override
	protected Iterable<UUID> getCompletionSuggestions(	CommandInput input, CommandContextView context,
														int minimumCompletionInput, String idPrefix) {
		return PlayerUUIDArgument.getDefaultCompletionSuggestions(input, context, minimumCompletionInput, idPrefix, filter);
	}
}
