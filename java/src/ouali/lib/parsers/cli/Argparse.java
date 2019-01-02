package ouali.lib.parsers.cli;

/*
 
Description:
A lightweight Java class for parsing command line arguments. Three types of argument are considered: 
	1. Required arguments: the program will quit and display an error message if one of these arguments is missing. 
	2. Optional arguments: the program will use the default value if the argument is not provided, otherwise the command line value will be used. 
	3. Flag arguments: flag arguments are supposed to be false by default, for flexilibility they can be set initially to true. If a they are called 
	in the	command line, their value will be set to true.

One can extend the Argparse class to override version(), help() and credits() methods.

Usage:
	
	// create an Argparser object 
	Argparse argparser = new Argparse(1);
	
	// define your arguments and their default value
	argparser.addRequiredArgument("-p", "string");
	argparser.addRequiredArgument("--long-arg", "1");
	argparser.addOptionalArgument("-optional", "1.618");
	argparser.addFlagArgument("-flag", false);
	
	// parsing the command line
	argparser.getArguments(args);
	
	// getting the values
	String string = argparser.getArgumentValue("-p");
	int integer = Integer.parseInt(argparser.getArgumentValue("--long-arg"));
	double real = Double.parseDouble(argparser.getArgumentValue("-optional"));
	boolean modelPath = argparser.getFlagValue("-flag");



Copyright 2019 Abdelkader Ouali

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
OF SUCH DAMAGE.

*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Argparse {
	protected Map<String, String> requiredValuedArguments;
	protected Map<String, String> optionalValuedArguments;
	protected Map<String, Boolean> flagArguments;
	protected int verbocity;

	private static int exitCode = 1;

	public Argparse() {
		this.requiredValuedArguments = new HashMap<String, String>();
		this.optionalValuedArguments = new HashMap<String, String>();
		this.flagArguments = new HashMap<String, Boolean>();
		verbocity = 0;
	}

	public Argparse(int verbocity_) {
		this.requiredValuedArguments = new HashMap<String, String>();
		this.optionalValuedArguments = new HashMap<String, String>();
		this.flagArguments = new HashMap<String, Boolean>();
		verbocity = verbocity_;
	}

	public Argparse(Map<String, String> requiredValuedArguments, Map<String, String> optionalValuedArguments,
			Map<String, Boolean> flagArguments, int verbocity) {
		this.requiredValuedArguments = requiredValuedArguments;
		this.optionalValuedArguments = optionalValuedArguments;
		this.flagArguments = flagArguments;
		this.verbocity = verbocity;
	}

	protected void help() {
		System.out.println("Program param_name_1 value_1 param_flag param_name_1 value_ 2 ...");
	}

	protected void version() {
		System.out.println("0.0.0");
	}

	protected void credits() {
		System.out.println("Credits");
	}

	public void getArguments(String[] args) {
		if (verbocity == 2) {
			credits();
			version();
		}
		int argn = 0;
//		int required = 0;
		ArrayList<String> checkRequiredArgs = new ArrayList<String>();
		while (argn < args.length) {
			boolean rval = requiredValuedArguments.containsKey(args[argn]);
			if (rval) {
				if (argn < args.length - 1) {
					int prec = argn++;
					requiredValuedArguments.put(args[prec], args[argn]);
					checkRequiredArgs.add(args[prec]);
				} else {
					displayError("The required argument " + args[argn] + " needs a value!");
					System.exit(Argparse.exitCode);
				}
			} else {
				boolean oval = optionalValuedArguments.containsKey(args[argn]);
				if (oval) {
					if (argn < args.length - 1) {
						int prec = argn++;
						optionalValuedArguments.put(args[prec], args[argn]);
					} else {
						displayError("The optional argument " + args[argn] + " needs a value!");
						System.exit(Argparse.exitCode);
					}
				} else {
					boolean fval = flagArguments.containsKey(args[argn]);
					if (fval) {
						flagArguments.put(args[argn], true);
					}
				}
			}
			argn++;
		}
		if (checkRequiredArgs.size() != requiredValuedArguments.size()) {
			System.out.println("Could not find the following required parameters:");
			for (String key : requiredValuedArguments.keySet())
				if (!checkRequiredArgs.contains(key))
					System.out.println(key);
			if (verbocity > 1)
				help();
			System.exit(Argparse.exitCode);
		}
	}

	public void addRequiredArgument(String arg, String val) {
		requiredValuedArguments.put(arg, val);
	}

	public void addOptionalArgument(String arg, String val) {
		optionalValuedArguments.put(arg, val);
	}

	public void addFlagArgument(String arg, boolean val) {
		flagArguments.put(arg, val);
	}

	public String getArgumentValue(String arg) {
		boolean rval = requiredValuedArguments.containsKey(arg);
		if (rval)
			return requiredValuedArguments.get(arg);
		else {
			boolean oval = optionalValuedArguments.containsKey(arg);
			if (oval)
				return optionalValuedArguments.get(arg);
			else {
				displayError("You're searching an unknown argument (" + arg + ")! ");
				System.exit(Argparse.exitCode);
			}

		}
		return null;
	}

	public boolean getFlagValue(String arg) {
		boolean rval = flagArguments.containsKey(arg);
		if (rval)
			return flagArguments.get(arg);
		else {
			displayError("You're searching an unknown flag argument (" + arg + ")! ");
			System.exit(Argparse.exitCode);
		}
		return false;
	}

	private void displayError(String msg) {
		if (verbocity > 0) {
			System.out.println(msg);
			if (verbocity > 1)
				help();
		}
	}
};