package ouali.test;

import java.io.IOException;

import ouali.lib.parsers.cli.Argparse;

public class Test {

	public static void main(String[] args) throws IOException, Exception {
		Argparse argparser = new Argparse(1);
		argparser.addRequiredArgument("-p", "string");
		argparser.addRequiredArgument("--long-arg", "1");
		argparser.addOptionalArgument("-optional", "1.618");
		argparser.addFlagArgument("-flag", false);
		argparser.getArguments(args);
		String string = argparser.getArgumentValue("-p");
		int integer = Integer.parseInt(argparser.getArgumentValue("--long-arg"));
		double real = Double.parseDouble(argparser.getArgumentValue("-optional"));
		boolean modelPath = argparser.getFlagValue("-flag");

		System.out.println(string + " " + integer + " " + real + " " + modelPath);

	}

}
