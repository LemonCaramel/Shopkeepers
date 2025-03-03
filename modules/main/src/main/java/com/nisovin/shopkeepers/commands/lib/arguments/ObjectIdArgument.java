package com.nisovin.shopkeepers.commands.lib.arguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.nisovin.shopkeepers.commands.lib.ArgumentFilter;
import com.nisovin.shopkeepers.commands.lib.ArgumentParseException;
import com.nisovin.shopkeepers.commands.lib.ArgumentRejectedException;
import com.nisovin.shopkeepers.commands.lib.ArgumentsReader;
import com.nisovin.shopkeepers.commands.lib.Command;
import com.nisovin.shopkeepers.commands.lib.CommandArgument;
import com.nisovin.shopkeepers.commands.lib.CommandContextView;
import com.nisovin.shopkeepers.commands.lib.CommandInput;
import com.nisovin.shopkeepers.util.java.Validate;

/**
 * Base class for arguments that accept some form of object identifier (eg. UUID or name).
 * <p>
 * By default this argument accepts any identifier that can be parsed by the wrapped identifier argument.
 * <p>
 * Sub-classes need to override {@link #getCompletionSuggestions(CommandInput, CommandContextView, String)} and
 * {@link #toString(Object)} to provide completion suggestions for partial inputs matching known ids.
 *
 * @param <I>
 *            the identifier type
 */
public abstract class ObjectIdArgument<I> extends CommandArgument<I> {

	protected static final Pattern ARGUMENTS_SEPARATOR_PATTERN = Pattern.compile(Command.ARGUMENTS_SEPARATOR, Pattern.LITERAL);

	protected final CommandArgument<I> idArgument;
	protected final ArgumentFilter<I> filter; // Not null

	// Some types of object id arguments may want to provide suggestions even for an empty partial input, while others
	// might want to limit their suggestions to the case that there is at least a minimum sized input (eg. if there
	// would otherwise be lots of candidate ids to suggest). This setting controls how many characters the input has to
	// at least consist of in order for the argument to provide completion suggestions.
	// However, some arguments may ignore this setting for some of their suggestions (Example: An entity uuid argument
	// may want to always suggest the uuid of the targeted entity, but only suggest other entity uuids if the input is a
	// certain length).
	// Set this to 0 to deactivate it (i.e. to provide completions even for empty prefixes).
	protected final int minimumCompletionInput;

	public ObjectIdArgument(String name, CommandArgument<I> idArgument, ArgumentFilter<I> filter, int minimumCompletionInput) {
		super(name);
		Validate.notNull(idArgument, "idArgument is null");
		Validate.isTrue(minimumCompletionInput >= 0, "minimumCompletionInput cannot be negative");
		this.idArgument = idArgument;
		idArgument.setParent(this);
		this.filter = (filter == null) ? ArgumentFilter.acceptAny() : filter;
		this.minimumCompletionInput = minimumCompletionInput;
	}

	@Override
	public I parseValue(CommandInput input, CommandContextView context, ArgumentsReader argsReader) throws ArgumentParseException {
		// Prefer this class's missing-argument exception over the id-argument's exception:
		if (!argsReader.hasNext()) {
			throw this.missingArgumentError();
		}
		int startIndex = argsReader.getCursor();
		// Throws exceptions with appropriate messages if the id cannot be parsed:
		I id = idArgument.parseValue(input, context, argsReader);

		// Check if id is accepted:
		if (!filter.test(id)) {
			int endIndex = argsReader.getCursor();
			List<String> parsedArgs = argsReader.getArgs().subList(startIndex + 1, endIndex + 1);
			String parsedArgsString = String.join(Command.ARGUMENTS_SEPARATOR, parsedArgs);
			throw new ArgumentRejectedException(this, filter.getInvalidArgumentErrorMsg(this, parsedArgsString, id));
		}
		return id;
	}

	// This gets applied to convert id completion suggestions to Strings.
	protected abstract String toString(I id);

	@Override
	public List<String> complete(CommandInput input, CommandContextView context, ArgumentsReader argsReader) {
		if (argsReader.getRemainingSize() == 0) {
			// There are no remaining arguments to complete.
			return Collections.emptyList();
		}

		// Try to parse id:
		int startIndex = argsReader.getCursor() + 1; // inclusive
		try {
			idArgument.parseValue(input, context, argsReader);
			if (argsReader.getRemainingSize() > 0) {
				// Successfully parsed and there are still arguments left, so this is not consuming the last argument:
				return Collections.emptyList();
			}
		} catch (ArgumentParseException e) {
		}

		// Determine id prefix and args count:
		List<String> args = argsReader.getArgs();
		int endIndex = args.size(); // exclusive
		// The partial id input may consist of multiple joined input arguments:
		int argsCount = (endIndex - startIndex);
		assert argsCount > 0; // Otherwise we would have no remaining arguments in the first place.

		String idPrefix;
		if (argsCount == 1) { // Single argument
			idPrefix = args.get(startIndex);
		} else { // Joined arguments:
			List<String> parsedArgs = args.subList(startIndex, endIndex);
			idPrefix = String.join(Command.ARGUMENTS_SEPARATOR, parsedArgs);
		}

		// Get completion suggestions:
		return this.complete(input, context, idPrefix, argsCount);
	}

	// argsCount: The number of arguments the id prefix consist of (>= 1).
	protected List<String> complete(CommandInput input, CommandContextView context, String idPrefix, int argsCount) {
		// Note: We don't check the minimumCompletionInput here but let getCompletionSuggestions handle it, because the
		// argument may want to ignore the minimumCompletionInput for some of its suggestions.
		List<String> suggestions = new ArrayList<>();
		for (I id : this.getCompletionSuggestions(input, context, idPrefix)) {
			if (suggestions.size() >= MAX_SUGGESTIONS) break;
			if (!filter.test(id)) continue; // Skip rejected ids

			String idString = this.toString(id);
			if (idString == null || idString.isEmpty()) continue; // Skip invalid id string

			// If the id prefix consists of multiple joined input arguments, we skip the first (matching) parts and only
			// output the final part(s) of the completed id as completion suggestion:
			if (argsCount > 1) {
				String[] idStringParts = ARGUMENTS_SEPARATOR_PATTERN.split(idString, argsCount);
				// This should usually be true for valid (consistent) argsCount and suggestions:
				if (idStringParts.length == argsCount) {
					idString = idStringParts[argsCount - 1];
				} // Else: Fallback to using the complete idString.
			}
			suggestions.add(idString);
		}
		return Collections.unmodifiableList(suggestions);
	}

	/**
	 * Gets the completion suggestions for the given id prefix.
	 * <p>
	 * The given command input and context can be used to limit the scope of the considered ids.
	 * <p>
	 * This should take this argument's {@link #minimumCompletionInput} into account.
	 * <p>
	 * Before these suggestions are actually used, they may first have to also pass the id filter used by this
	 * {@link ObjectIdArgument}.
	 * 
	 * @param input
	 *            the command input, not <code>null</code>
	 * @param context
	 *            the command context, not <code>null</code>
	 * @param idPrefix
	 *            the id prefix, may be empty, not <code>null</code>
	 * @return the suggestions, not <code>null</code>
	 */
	protected abstract Iterable<I> getCompletionSuggestions(CommandInput input, CommandContextView context, String idPrefix);
}
