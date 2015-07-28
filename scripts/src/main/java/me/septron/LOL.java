package me.septron;

import com.inubot.bot.net.AccountCreator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Septron
 * @since July 25, 2015
 */
public class LOL implements Runnable {

	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private static final List<String> vocals = Arrays.asList(
			"a", "e", "i", "o", "u", "ei", "ai", "ou", "j", "ji", "y", "oi", "au", "oo"
	);

	private static final List<String> starts = Arrays.asList(
			"Ch", "Bl", "Br", "Fl", "Gr", "Kl", "Pr", "St", "Sh", "Th"
	);

	private static final List<String> ends = Arrays.asList(
			"ch", "gh", "nn", "st", "sh", "th", "tt", "ss", "pf", "nt"
	);

	private static int random(int min, int max) {
		return (int) (min + (Math.random() * (max + 1 - min)));
	}

	private static String random(List v) {
		return (String) v.get(random(0, v.size() - 1));
	}

	private static String generate() {
		StringBuilder builder = new StringBuilder();
		builder.append(random(starts));

		for (int i = 0; i < random(1, 3); i++) {
			builder.append(random(vocals));
		}

		builder.append(random(ends));
		return builder.toString();
	}

	public static void main(String... args) {
		executor.scheduleWithFixedDelay(new LOL(), 0, 2, TimeUnit.HOURS);
	}

	@Override
	public void run() {
		for (int i = 0; i < 5; i++) {
			String name = generate();
			String pass = String.valueOf("penis123");
			String email = name.replace(" ", ".").toLowerCase() + "@live.com";
			if (AccountCreator.create(email, name, pass)) {
				System.out.println("Email: " + email + " Username: " + name + " Password: " + pass);
			}
		}
	}
}
