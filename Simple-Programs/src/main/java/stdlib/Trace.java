//- Copyright (C) 2014  James Riely, DePaul University
//- Copyright (C) 2004  John Hamer, University of Auckland [Graphviz code]
//-
//-   This program is free software; you can redistribute it and/or
//-   modify it under the terms of the GNU General Public License
//-   as published by the Free Software Foundation; either version 2
//-   of the License, or (at your option) any later version.
//-
//-   This program is distributed in the hope that it will be useful,
//-   but WITHOUT ANY WARRANTY; without even the implied warranty of
//-   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//-   GNU General Public License for more details.
//-
//-   You should have received a copy of the GNU General Public License along
//-   with this program; if not, write to the Free Software Foundation, Inc.,
//-   59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.package algs;

//- Event monitoring code based on
//-    http://fivedots.coe.psu.ac.th/~ad/jg/javaArt5/
//-    By Andrew Davison, ad@fivedots.coe.psu.ac.th, March 2009
//-
//- Graphviz code based on LJV
//-    https://www.cs.auckland.ac.nz/~j-hamer/
//-    By John Hamer, <J.Hamer@cs.auckland.ac.nz>, 2003
//-    Copyright (C) 2004  John Hamer, University of Auckland

package stdlib;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import com.sun.jdi.*;
import com.sun.jdi.connect.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;

/**
 * <p>
 * Traces the execution of a target program.
 * </p><p>
 * See <a href="http://fpl.cs.depaul.edu/jriely/visualization/">http://fpl.cs.depaul.edu/jriely/visualization/</a>
 * </p><p>
 * Command-line usage: java Trace [OptionalJvmArguments] fullyQualifiedClassName
 * </p><p>
 * Starts a new JVM (java virtual machine) running the main program in
 * fullyQualifiedClassName, then traces it's behavior. The OptionalJvmArguments
 * are passed to this underlying JVM.
 * </p><p>
 * Example usages:
 * </p><pre>
 *   java Trace MyClass
 *   java Trace mypackage.MyClass
 *   java Trace -cp ".:/pathTo/Library.jar" mypackage.MyClass  // mac/linux
 *   java Trace -cp ".;/pathTo/Library.jar" mypackage.MyClass  // windows
 * </pre><p>
 * Two types of display are support: console and graphviz In order to use
 * graphziv, you must install http://www.graphviz.org/ and perhaps call
 * graphvizAddPossibleDotLocation to include the location of the "dot"
 * executable.
 * </p><p>
 * You can either draw the state at each step --- <code>drawSteps()</code> --- or
 * you can draw states selectively by calling <code>Trace.draw()</code> from the program
 * you are tracing. See the example in <code>ZTraceExample.java</code>.
 * </p>
 * @author James Riely, jriely@cs.depaul.edu, 2014-2015
 */
 /* !!!!!!!!!!!!!!!!!!!!!!!! COMPILATION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * This class requires Java 8. It also requires the file tools.jar, which comes
 * with the JDK (not the JRE)
 *
 * On windows, you can find it in
 *
 * <pre>
 *   C:\Program Files\Java\jdk...\lib\tools.jar
 * </pre>
 *
 * On mac, look in
 *
 * <pre>
 *   /Library/Java/JavaVirtualMachines/jdk.../Contents/Home/lib/tools.jar
 * </pre>
 *
 * To put this in your eclipse build path, select your project in the package
 * explorer, then:
 *
 * <pre>
 *  Project > Properties > Java Build Path > Libraries > Add External Library
 * </pre>
 *
 */

// TODO: Refactor to use GraphvizBuilder
public class Trace {
	private Trace () {} // noninstantiable class
	protected static final String CALLBACK_CLASS_NAME = Trace.class.getCanonicalName ();
	protected static final String GRAPHVIZ_CLASS_NAME = Graphviz.class.getCanonicalName ();

	/**
	 * Draw the given object.
	 *
	 * This is a stub method, which is trapped by the debugger. It only has an
	 * effect if a variant of Trace.run() has been called to start the debugger
	 * and Trace.drawSteps() is false.
	 */	
	public static void drawObject (Object object) { } // See Printer. methodEntryEvent	
	protected static final String CALLBACK_DRAW_OBJECT = "drawObject";
	protected static final HashSet<String> CALLBACKS = new HashSet<> ();
	static { CALLBACKS.add (CALLBACK_DRAW_OBJECT); }
	/**
	 * Draw the given object, labeling it with the given name.
	 *
	 * This is a stub method, which is trapped by the debugger. It only has an
	 * effect if a variant of Trace.run() has been called to start the debugger
	 * and Trace.drawSteps() is false.
	 */
	public static void drawObjectWithName (String name, Object object) { } // See Printer. methodEntryEvent
	protected static final String CALLBACK_DRAW_OBJECT_NAMED = "drawObjectWithName";
	static { CALLBACKS.add (CALLBACK_DRAW_OBJECT_NAMED); }
	/**
	 * Draw the given objects.
	 *
	 * This is a stub method, which is trapped by the debugger. It only has an
	 * effect if a variant of Trace.run() has been called to start the debugger
	 * and Trace.drawSteps() is false.
	 */
	public static void drawObjects (Object... objects) { } // See Printer. methodEntryEvent
	protected static final String CALLBACK_DRAW_OBJECTS = "drawObjects";
	static { CALLBACKS.add (CALLBACK_DRAW_OBJECTS); }
	/**
	 * Draw the given objects, labeling them with the given names. The array of
	 * namesAndObjects must alternate between names and objects, as in
	 * Trace.drawObjects("x", x, "y" y).
	 *
	 * This is a stub method, which is trapped by the debugger. It only has an
	 * effect if a variant of Trace.run() has been called to start the debugger
	 * and Trace.drawSteps() is false.
	 */
	public static void drawObjectsWithNames (Object... namesAndObjects) { }  // See Printer. methodEntryEvent
	protected static final String CALLBACK_DRAW_OBJECTS_NAMED = "drawObjectsWithNames";
	static { CALLBACKS.add (CALLBACK_DRAW_OBJECTS_NAMED); }

	/**
	 * Draw the current frame, as well as all reachable objects.
	 *
	 * This is a stub method, which is trapped by the debugger. It only has an
	 * effect if a variant of Trace.run() has been called to start the debugger
	 * and Trace.drawSteps() is false.
	 */
	public static void drawThisFrame () { }  // See Printer. methodEntryEvent
	protected static final String CALLBACK_DRAW_THIS_FRAME = "drawThisFrame";
	static { CALLBACKS.add (CALLBACK_DRAW_THIS_FRAME); }

	/**
	 * Stop drawing steps.
	 *
	 * This is a stub method, which is trapped by the debugger. It only has an
	 * effect if a variant of Trace.run() has been called to start the debugger.
	 */
	public static void drawStepsEnd () { }  // See Printer. methodEntryEvent
	protected static final String CALLBACK_DRAW_STEPS_END = "drawStepsEnd";
	static { CALLBACKS.add (CALLBACK_DRAW_STEPS_END); }

	/**
	 * Start drawing steps.
	 *
	 * This is a stub method, which is trapped by the debugger. It only has an
	 * effect if a variant of Trace.run() has been called to start the debugger.
	 */
	public static void drawSteps () { }  // See Printer. methodEntryEvent
	protected static final String CALLBACK_DRAW_STEPS_BEGIN = "drawSteps";
	static { CALLBACKS.add (CALLBACK_DRAW_STEPS_BEGIN); }

	/**
	 * Draw all stack frames and static variables, as well as all reachable
	 * objects.
	 *
	 * This is a stub method, which is trapped by the debugger. It only has an
	 * effect if a variant of Trace.run() has been called to start the debugger
	 * and Trace.drawSteps() is false.
	 */
	public static void draw () { }  // See Printer. methodEntryEvent
	protected static final String CALLBACK_DRAW_ALL_FRAMES = "draw";
	static { CALLBACKS.add (CALLBACK_DRAW_ALL_FRAMES); }
	
	/**
	 * Clear the call tree, removing all previous entries.
	 *
	 * This is a stub method, which is trapped by the debugger. It only has an
	 * effect if a variant of Trace.run() has been called to start the debugger.
	 */
	public static void clearCallTree () { }  // See Printer. methodEntryEvent
	protected static final String CALLBACK_CLEAR_CALL_TREE = "clearCallTree";

	// Basic graphviz options
	/**
	 * Run graphviz "dot" program to produce an output file (default==true). If
	 * false, a graphviz source file is created, but no graphic file is
	 * generated.
	 */
	public static void graphvizRunGraphviz (boolean value) {
		GRAPHVIZ_RUN_GRAPHVIZ = value;
	}
	protected static boolean GRAPHVIZ_RUN_GRAPHVIZ = true;

	/**
	 * The graphviz format -- see http://www.graphviz.org/doc/info/output.html .
	 * (default=="png").
	 */
	public static void graphvizOutputFormat (String value) {
		GRAPHVIZ_OUTPUT_FORMAT = value;
	}
	protected static String GRAPHVIZ_OUTPUT_FORMAT = "png";

	/**
	 * Sets the graphviz output directory.
	 * Creates the directory if necessary.
	 * Relative pathnames are interpreted with respect to the user's home directory.
	 * Default is "Desktop/GraphvizOutput".
	 */
	public static void setGraphizOutputDir (String dirName) {
		GRAPHVIZ_DIR = dirName;
	}
	private static String GRAPHVIZ_DIR = "Desktop" + File.separator + "GraphvizOutput";

	/**
	 * Sets the console output to the given filename. Console output will be
	 * written to:
	 *
	 * <pre>
	 * (user home directory)/(filename)
	 * </pre>
	 */
	public static void setConsoleFilenameRelativeToUserHome (String filename) {
		setConsoleFilename (System.getProperty ("user.home") + File.separator + filename);
	}
	/**
	 * Sets the console output to the given filename.
	 */
	public static void setConsoleFilename (String filename) {
		Printer.setFilename (filename);
	}
	/**
	 * Sets the console output to the default (the terminal).
	 */
	public static void setConsoleFilename () {
		Printer.setFilename ();
	}

	// Basic options --
	protected static boolean GRAPHVIZ_SHOW_STEPS = false;
//	protected static void drawStepsOf (String className, String methodName) {
//		GRAPHVIZ_SHOW_STEPS = true;
//		if (GRAPHVIZ_SHOW_STEPS_OF == null)
//			GRAPHVIZ_SHOW_STEPS_OF = new HashSet<>();
//		GRAPHVIZ_SHOW_STEPS_OF.add (new OptionalClassNameWithRequiredMethodName (className, methodName));
//		REPRESS_RETURN_ON_GRAPHVIZ_SHOW_STEPS_OF = (GRAPHVIZ_SHOW_STEPS_OF.size () <= 1);
//	}
	/**
	 * Create a new graphviz drawing for every step of the named method.
	 * The methodName does not include parameters.
	 * In order to show a constructor, use the method name "<init>".
	 */
	public static void drawStepsOfMethod (String methodName) { }  // See Printer. methodEntryEvent
	/**
	 * Create a new graphviz drawing for every step of the named methods.
	 * The methodName does not include parameters.
	 * In order to show a constructor, use the method name "<init>".
	 */
	public static void drawStepsOfMethods (String... methodName) { }  // See Printer. methodEntryEvent
	protected static final String CALLBACK_DRAW_STEPS_OF_METHOD = "drawStepsOfMethod";
	static { CALLBACKS.add (CALLBACK_DRAW_STEPS_OF_METHOD); }
	protected static final String CALLBACK_DRAW_STEPS_OF_METHODS = "drawStepsOfMethods";
	static { CALLBACKS.add (CALLBACK_DRAW_STEPS_OF_METHODS); }
	protected static void drawStepsOfMethodBegin (String methodName) {
		GRAPHVIZ_SHOW_STEPS = true;
		if (GRAPHVIZ_SHOW_STEPS_OF == null)
			GRAPHVIZ_SHOW_STEPS_OF = new HashSet<>();
		GRAPHVIZ_SHOW_STEPS_OF.add (new OptionalClassNameWithRequiredMethodName (null, methodName));
		REPRESS_RETURN_ON_GRAPHVIZ_SHOW_STEPS_OF = (GRAPHVIZ_SHOW_STEPS_OF.size () <= 1);
	}
	protected static void drawStepsOfMethodEnd () {
		GRAPHVIZ_SHOW_STEPS = true;
		GRAPHVIZ_SHOW_STEPS_OF = new HashSet<>();
		REPRESS_RETURN_ON_GRAPHVIZ_SHOW_STEPS_OF = false;
	}

	protected static boolean drawStepsOfInternal (ThreadReference thr) {
		if (GRAPHVIZ_SHOW_STEPS_OF == null) return true;
		List<StackFrame> frames = null;
		try { frames = thr.frames (); } catch (IncompatibleThreadStateException e) { }
		return Trace.drawStepsOfInternal (frames, null);
	}
	protected static boolean drawStepsOfInternal (List<StackFrame> frames, Value returnVal) {
		if (GRAPHVIZ_SHOW_STEPS_OF == null) return true;
		if (frames == null) return true;
		if (REPRESS_RETURN_ON_GRAPHVIZ_SHOW_STEPS_OF && returnVal != null && !(returnVal instanceof VoidValue)) return false;
		StackFrame currentFrame = frames.get (0);
		String className = currentFrame.location ().declaringType ().name ();
		String methodName = currentFrame.location ().method ().name ();
		return Trace.drawStepsOfInternal (className, methodName);
	}
	protected static boolean drawStepsOfInternal (String className, String methodName) {
		if (GRAPHVIZ_SHOW_STEPS_OF == null) return true;
		//System.err.println (className + "." + methodName + " " + new OptionalClassNameWithRequiredMethodName(className, methodName).hashCode() + " "+ GRAPHVIZ_SHOW_STEPS_OF);
		return GRAPHVIZ_SHOW_STEPS_OF.contains (new OptionalClassNameWithRequiredMethodName(className, methodName));
	}
	protected static Set<OptionalClassNameWithRequiredMethodName> GRAPHVIZ_SHOW_STEPS_OF = null;
	private static boolean REPRESS_RETURN_ON_GRAPHVIZ_SHOW_STEPS_OF = false;
	protected static class OptionalClassNameWithRequiredMethodName {
		String className;
		String methodName;
		public OptionalClassNameWithRequiredMethodName (String className, String methodName) {
			this.className = className;
			this.methodName = methodName;
		}
		public String toString () {
			return className + "." + methodName;
		}
		public boolean equals (Object other) {
			//System.err.println (this + "==" + other);
			if (other == this) return true;
			if (other == null) return false;
			if (other.getClass () != this.getClass ()) return false;
			OptionalClassNameWithRequiredMethodName that = (OptionalClassNameWithRequiredMethodName) other;
			if (this.className != null && that.className != null) {
				if (! this.className.equals (that.className)) return false;
			}
			if (! this.methodName.equals (that.methodName)) return false;
			return true;
		}
		public int hashCode() { return methodName.hashCode (); }
	}
	/**
	 * Show events on the console (default==false).
	 */
	public static void consoleShow (boolean value) {
		CONSOLE_SHOW_THREADS = value;
		CONSOLE_SHOW_CLASSES = value;
		CONSOLE_SHOW_CALLS = value;
		CONSOLE_SHOW_STEPS = value;
		CONSOLE_SHOW_VARIABLES = value;
		CONSOLE_SHOW_STEPS_VERBOSE = false;
	}
	/**
	 * Show events on the console, including code (default==false).
	 */
	public static void consoleShowVerbose (boolean value) {
		CONSOLE_SHOW_THREADS = value;
		CONSOLE_SHOW_CLASSES = value;
		CONSOLE_SHOW_CALLS = value;
		CONSOLE_SHOW_STEPS = value;
		CONSOLE_SHOW_VARIABLES = value;
		CONSOLE_SHOW_STEPS_VERBOSE = true;
	}
	protected static boolean CONSOLE_SHOW_THREADS = false;
	protected static boolean CONSOLE_SHOW_CLASSES = false;
	protected static boolean CONSOLE_SHOW_CALLS = false;
	protected static boolean CONSOLE_SHOW_STEPS = false;
	protected static boolean CONSOLE_SHOW_STEPS_VERBOSE = false;
	protected static boolean CONSOLE_SHOW_VARIABLES = false;
	/**
	 * Show String, Integer, Double, etc as simplified objects (default==false).
	 */
	public static void showBuiltInObjects (boolean value) {
		SHOW_STRINGS_AS_PRIMITIVE = !value;
		SHOW_BOXED_PRIMITIVES_AS_PRIMITIVE = !value;
		GRAPHVIZ_SHOW_BOXED_PRIMITIVES_SIMPLY = true;
	}
	/**
	 * Show String, Integer, Double, etc as regular objects (default==false).
	 */
	public static void showBuiltInObjectsVerbose (boolean value) {
		SHOW_STRINGS_AS_PRIMITIVE = !value;
		SHOW_BOXED_PRIMITIVES_AS_PRIMITIVE = !value;
		GRAPHVIZ_SHOW_BOXED_PRIMITIVES_SIMPLY = false;
	}
	protected static boolean SHOW_STRINGS_AS_PRIMITIVE = true;
	protected static boolean SHOW_BOXED_PRIMITIVES_AS_PRIMITIVE = true;
	protected static boolean GRAPHVIZ_SHOW_BOXED_PRIMITIVES_SIMPLY = true;

	/**
	 * Run the debugger on the current class. Execution of the current program
	 * ends and a new JVM is started under the debugger.
	 *
	 * The debugger will execute the main method of the class that calls run.
	 *
	 * The main method is run with no arguments.
	 */
	public static void run () {
		Trace.run (new String[] {});
	}
	/**
	 * Run the debugger on the current class. Execution of the current program
	 * ends and a new JVM is started under the debugger.
	 *
	 * The debugger will execute the main method of the class that calls run.
	 *
	 * The main method is run with the given arguments.
	 */
	public static void run (String[] args) {
		StackTraceElement[] stackTrace = Thread.currentThread ().getStackTrace ();
		String mainClassName = stackTrace[stackTrace.length - 1].getClassName ();
		Trace.internalPrepAndRun (mainClassName, args, true);
	}
	/**
	 * Run the debugger on the given class. The current program will continue to
	 * execute after this call to run has completed.
	 *
	 * The main method of the given class is called with no arguments.
	 */
	public static void run (String mainClassName) {
		Trace.internalPrepAndRun (mainClassName, new String[] {}, false);
	}
	/**
	 * Run the debugger on the given class. The current program will continue to
	 * execute after this call to run has completed.
	 *
	 * The main method of the given class is called with no arguments.
	 */
	public static void run (Class<?> mainClass) {
		Trace.run (mainClass, new String[] {});
	}
	/**
	 * Run the debugger on the given class. The current program will continue to
	 * execute after this call to run has completed.
	 *
	 * The main method of the given class is called with the given arguments.
	 */
	public static void run (String mainClassName, String[] args) {
		internalPrepAndRun (mainClassName, args, false);
	}
	/**
	 * Run the debugger on the given class. The current program will continue to
	 * execute after this call to run has completed.
	 *
	 * The main method of the given class is called with the given arguments.
	 */
	public static void run (Class<?> mainClass, String[] args) {
		Trace.internalPrepAndRun (mainClass.getCanonicalName (), args, false);
	}
	/**
	 * Run the debugger on the given class. The current program will continue to
	 * execute after this call to run has completed.
	 *
	 * The main method of the given class is called with the given arguments.
	 */
	public static void runWithArgs (Class<?> mainClass, String... args) {
		Trace.run (mainClass, args);
	}
	/**
	 * Run the debugger on the given class. The current program will continue to
	 * execute after this call to run has completed.
	 *
	 * The main method of the given class is called with the given arguments.
	 */
	public static void runWithArgs (String mainClassName, String... args) {
		Trace.internalPrepAndRun (mainClassName, args, false);
	}
	/**
	 * The debugger can be invoked from the command line using this method. The
	 * first argument must be the fully qualified name of the class to be
	 * debugged. Addition arguments are passed to the main method of the class
	 * to be debugged.
	 */
	public static void main (String[] args) {
		if (args.length == 0) {
			System.err.println ("Usage: java " + Trace.class.getCanonicalName () + " [OptionalJvmArguments] fullyQualifiedClassName");
			System.exit (-1);
		}
		int length = PREFIX_ARGS_FOR_VM.size ();
		String[] allArgs = new String[length + args.length];
		for (int i = 0; i < length; i++)
			allArgs[i] = PREFIX_ARGS_FOR_VM.get (i);
		System.arraycopy (args, 0, allArgs, length, args.length);
		internalRun ("Trace", allArgs, false);
	}

	//------------------------------------------------------------------------
	//          _______.___________.  ______   .______      __     __
	//         /       |           | /  __  \  |   _  \    |  |   |  |
	//        |   (----`---|  |----`|  |  |  | |  |_)  |   |  |   |  |
	//         \   \       |  |     |  |  |  | |   ___/    |  |   |  |
	//     .----)   |      |  |     |  `--'  | |  |        |__|   |__|
	//     |_______/       |__|      \______/  | _|        (__)   (__)
	//
	//          _______.  ______     ___      .______     ____    ____
	//         /       | /      |   /   \     |   _  \    \   \  /   /
	//        |   (----`|  ,----'  /  ^  \    |  |_)  |    \   \/   /
	//         \   \    |  |      /  /_\  \   |      /      \_    _/
	//     .----)   |   |  `----./  _____  \  |  |\  \----.   |  |
	//     |_______/     \______/__/     \__\ | _| `._____|   |__|
	//
	//     .___________. __    __   __  .__   __.   _______      _______.
	//     |           ||  |  |  | |  | |  \ |  |  /  _____|    /       |
	//     `---|  |----`|  |__|  | |  | |   \|  | |  |  __     |   (----`
	//         |  |     |   __   | |  | |  . `  | |  | |_ |     \   \
	//         |  |     |  |  |  | |  | |  |\   | |  |__| | .----)   |
	//         |__|     |__|  |__| |__| |__| \__|  \______| |_______/
	//
	//     .______    _______  __        ______   ____    __    ____    __
	//     |   _  \  |   ____||  |      /  __  \  \   \  /  \  /   /   |  |
	//     |  |_)  | |  |__   |  |     |  |  |  |  \   \/    \/   /    |  |
	//     |   _  <  |   __|  |  |     |  |  |  |   \            /     |  |
	//     |  |_)  | |  |____ |  `----.|  `--'  |    \    /\    /      |__|
	//     |______/  |_______||_______| \______/      \__/  \__/       (__)
	//
	//------------------------------------------------------------------------
	// This program uses all sorts of crazy java foo.
	// You should not have to read anything else in this file.

	/**
	 * This code is based on the Trace.java example included in the
	 * demo/jpda/examples.jar file in the JDK.
	 *
	 * For more information on JPDA and JDI, see:
	 *
	 * <pre>
	 * http://docs.oracle.com/javase/8/docs/technotes/guides/jpda/trace.html
	 * http://docs.oracle.com/javase/8/docs/jdk/api/jpda/jdi/index.html
	 * http://forums.sun.com/forum.jspa?forumID=543
	 * </pre>
	 *
	 * Changes made by Riely:
	 *
	 * - works with packages other than default
	 *
	 * - prints values of variables Objects values are printed as "@uniqueId"
	 * Arrays include the values in the array, up to
	 *
	 * - handles exceptions
	 *
	 * - works for arrays, when referenced by local variables, static fields, or
	 * fields of "this"
	 *
	 * - options for more or less detail
	 *
	 * - indenting to show position in call stack
	 *
	 * - added methods to draw the state of the system using graphviz
	 *
	 * Known bugs/limitations:
	 *
	 * - There appears to be a bug in the JDI: steps from static initializers
	 * are not always reported. I don't see a real pattern here. Some static
	 * initializers work and some don't. When the step event is not generated by
	 * the JDI, this code cannot report on it, of course, since we rely on the
	 * JDI to generate the steps.
	 *
	 * - Works for local arrays, including updates to fields of "this", but will
	 * not print changes through other object references, such as
	 * yourObject.publicArray[0] = 22 As long as array fields are private (as
	 * they should be), it should be okay.
	 *
	 * - Updates to arrays that are held both in static fields and also in local
	 * variables or object fields will be shown more than once in the console
	 * view.
	 *
	 * - Space leak: copies of array references are kept forever. See
	 * "registerArray".
	 *
	 * - Not debugged for multithreaded code. Monitor events are currently
	 * ignored.
	 *
	 * - Slow. Only good for short programs.
	 *
	 * - This is a hodgepodge of code from various sources, not well debugged,
	 * not super clean.
	 *
	 */
	/**
	 * Macintosh OS-X sometimes sets the hostname to an unroutable name and this
	 * may cause the socket connection to fail. To see your hostname, open a
	 * Terminal window and type the "hostname" command. On my machine, the
	 * terminal prompt is "$", and so the result looks like this:
	 *
	 * <pre>
	 *   $ hostname
	 *   escarole.local
	 *   $
	 * </pre>
	 *
	 * To see that this machine is routable, I can "ping" it:
	 *
	 * <pre>
	 *   $ ping escarole.local
	 *   PING escarole.local (192.168.1.109): 56 data bytes
	 *   64 bytes from 192.168.1.109: icmp_seq=0 ttl=64 time=0.046 ms
	 *   64 bytes from 192.168.1.109: icmp_seq=1 ttl=64 time=0.104 ms
	 *   ^C
	 *   --- escarole.local ping statistics ---
	 *   2 packets transmitted, 2 packets received, 0.0% packet loss
	 *   round-trip min/avg/max/stddev = 0.046/0.075/0.104/0.029 ms
	 * </pre>
	 *
	 * When I am connected to some networks, the result is like this:
	 *
	 * <pre>
	 *   $ hostname
	 *   loop-depaulsecure-182-129.depaulsecure-employee.depaul.edu
	 *   $ ping loop-depaulsecure-182-129.depaulsecure-employee.depaul.edu
	 *   ping: cannot resolve loop-depaulsecure-182-129.depaulsecure-employee.depaul.edu: Unknown host
	 * </pre>
	 *
	 * Or this:
	 *
	 * <pre>
	 *   $ hostname
	 *   asteelembook.cstcis.cti.depaul.edu
	 *   $ ping asteelembook.cstcis.cti.depaul.edu
	 *   PING asteelembook.cstcis.cti.depaul.edu (140.192.38.100): 56 data bytes
	 *   Request timeout for icmp_seq 0
	 *   Request timeout for icmp_seq 1
	 *   ^C
	 *   --- asteelembook.cstcis.cti.depaul.edu ping statistics ---
	 *   3 packets transmitted, 0 packets received, 100.0% packet loss
	 * </pre>
	 *
	 * To stop OS-X from taking bogus hostname like this, you can fix the
	 * hostname, as follows:
	 *
	 * <pre>
	 *   $ scutil --set HostName escarole.local
	 *   $
	 * </pre>
	 *
	 * Where "escarole" is your computer name (no spaces or punctuation). You
	 * will be prompted for your password in order to modify the configuration.
	 *
	 * To reset OS-X to it's default behavior, do this:
	 *
	 * <pre>
	 *   $ scutil --set HostName ""
	 *   $
	 * </pre>
	 *
	 * On OSX 10.10 (Yosemite), apple seems to have turned of DNS lookup for
	 * .local addresses.
	 *
	 * https://discussions.apple.com/thread/6611817?start=13
	 *
	 * To fix this, you need to
	 *
	 * <pre>
	 * sudo vi /etc/hosts
	 * </pre>
	 *
	 * and change the line
	 *
	 * <pre>
	 *   127.0.0.1       localhost
	 * </pre>
	 *
	 * to
	 *
	 * <pre>
	 *   127.0.0.1       localhost escarole.local
	 * </pre>
	 *
	 * More robustly, the following "patch" fixes the JDI so that it uses the ip
	 * address, rather than the hostname. The code is called from
	 *
	 * <pre>
	 * com.sun.tools.jdi.SunCommandLineLauncher.launch ()
	 * </pre>
	 *
	 * which calls
	 *
	 * <pre>
	 * com.sun.tools.jdi.SocketTransportService.SocketListenKey.address ()
	 * </pre>
	 *
	 * Here is the patch. Just compile this and put in your classpath before
	 * tools.jar.
	 *
	 * <pre>
	 * package com.sun.tools.jdi;
	 *
	 * import java.net.*;
	 *
	 * class SocketTransportService$SocketListenKey extends com.sun.jdi.connect.spi.TransportService.ListenKey {
	 *     ServerSocket ss;
	 *     SocketTransportService$SocketListenKey (ServerSocket ss) {
	 *         this.ss = ss;
	 *     }
	 *     ServerSocket socket () {
	 *         return ss;
	 *     }
	 *     public String toString () {
	 *         return address ();
	 *     }
	 *
	 *     // Returns the string representation of the address that this listen key represents.
	 *     public String address () {
	 *         InetAddress address = ss.getInetAddress ();
	 *
	 *         // If bound to the wildcard address then use current local hostname. In
	 *         // the event that we don't know our own hostname then assume that host
	 *         // supports IPv4 and return something to represent the loopback address.
	 *         if (address.isAnyLocalAddress ()) {
	 *             // JWR: Only change is to comment out the lines below
	 *             // try {
	 *             //     address = InetAddress.getLocalHost ();
	 *             // } catch (UnknownHostException uhe) {
	 *             byte[] loopback = { 0x7f, 0x00, 0x00, 0x01 };
	 *             try {
	 *                 address = InetAddress.getByAddress (&quot;127.0.0.1&quot;, loopback);
	 *             } catch (UnknownHostException x) {
	 *                 throw new InternalError (&quot;unable to get local hostname&quot;);
	 *             }
	 *             //  }
	 *         }
	 *
	 *         // Now decide if we return a hostname or IP address. Where possible
	 *         // return a hostname but in the case that we are bound to an address
	 *         // that isn't registered in the name service then we return an address.
	 *         String result;
	 *         String hostname = address.getHostName ();
	 *         String hostaddr = address.getHostAddress ();
	 *         if (hostname.equals (hostaddr)) {
	 *             if (address instanceof Inet6Address) {
	 *                 result = &quot;[&quot; + hostaddr + &quot;]&quot;;
	 *             } else {
	 *                 result = hostaddr;
	 *             }
	 *         } else {
	 *             result = hostname;
	 *         }
	 *
	 *         // Finally return &quot;hostname:port&quot;, &quot;ipv4-address:port&quot; or &quot;[ipv6-address]:port&quot;.
	 *         return result + &quot;:&quot; + ss.getLocalPort ();
	 *     }
	 * }
	 * </pre>
	 */
	private static String IN_DEBUGGER = "TraceDebuggingVMHasLaunched";
	private static boolean insideTestVM () {
		return System.getProperty (IN_DEBUGGER) != null;
	}
	/**
	 * Prepares the args and then calls internalRun.
	 */
	private static void internalPrepAndRun (String mainClassName, String[] args, boolean terminateAfter) {		
		int length = PREFIX_ARGS_FOR_VM.size ();
		String[] allArgs = new String[length + args.length + 1];
		for (int i = 0; i < length; i++)
			allArgs[i] = PREFIX_ARGS_FOR_VM.get (i);
		allArgs[length] = mainClassName;
		System.arraycopy (args, 0, allArgs, length + 1, args.length);
		internalRun (mainClassName, allArgs, terminateAfter);
	}
	/**
	 * This is the function that starts the JVM. If terminateAfter is true, then
	 * the current thread is killed after the debug JVM terminates.
	 */
	@SuppressWarnings("deprecation")
	private static void internalRun (String mainClassName, String[] allArgs, boolean terminateAfter) {
		if (insideTestVM ()) return;
		Graphviz.setOutputDirectory (GRAPHVIZ_DIR, mainClassName);
		VirtualMachine vm = launchConnect (allArgs);
		monitorJVM (vm);
		if (terminateAfter) Thread.currentThread ().stop ();
	}

	/**
	 * Prefix options for the debugger VM. By default, the classpath is set to
	 * the current classpath. Other options can be provided here.
	 */
	public static void addPrefixOptionsForVm (String value) {
		PREFIX_ARGS_FOR_VM.add (value);
	}
	private static String BIN_CLASSPATH = "bin" + System.getProperty ("path.separator") + ".";
	protected static ArrayList<String> PREFIX_ARGS_FOR_VM;
	static {
		PREFIX_ARGS_FOR_VM = new ArrayList<> ();
		PREFIX_ARGS_FOR_VM.add ("-cp");
		PREFIX_ARGS_FOR_VM.add ("\"" + System.getProperty ("java.class.path") + "\"");
		PREFIX_ARGS_FOR_VM.add ("-D" + IN_DEBUGGER + "=true");
	}
	/**
	 * Turn on debugging information (default==false). Intended for developers.
	 */
	public static void debug (boolean value) {
		DEBUG = value;
	}
	protected static boolean DEBUG = false;

	/**
	 * Add an exclude pattern. Classes whose fully qualified name matches an
	 * exclude pattern are ignored by the debugger. Regular expressions are
	 * limited to exact matches and patterns that begin with '*' or end with
	 * '*'; for example, "*.Foo" or "java.*". This limitation is inherited from
	 * <code>com.sun.jdi.request.WatchpointRequest</code>. The default exclude
	 * patterns include:
	 *
	 * <pre>
	 *  "*$$Lambda$*" "java.*" "jdk.*" "sun.*"  "com.*"  "org.*"  "javax.*"  "apple.*"  "Jama.*"  "qs.*"
	 *  "stdlib.A*" "stdlib.B*" "stdlib.C*" "stdlib.D*" "stdlib.E*" "stdlib.F*" "stdlib.G*" "stdlib.H*" 
	 *  "stdlib.I*" "stdlib.J*" "stdlib.K*" "stdlib.L*" "stdlib.M*" "stdlib.N*" "stdlib.O*" "stdlib.P*" 
	 *  "stdlib.Q*" "stdlib.R*" "stdlib.S*" 
	 *  "stdlib.U*" "stdlib.V*" "stdlib.W*" "stdlib.X*" "stdlib.Y*" 
	 * </pre>
	 * 
	 * The JDI excludes classes, but does not allow exceptions. This is the
	 * reason for the unusual number of excludes for <code>stdlib</code>. It is important that
	 * <code>stdlib.Trace</code> not be excluded --- if it were, then callBacks
	 * to <code>Trace.draw</code> would not function.  As a result, all classes in <code>stdlib</code> that start with a letter other than <code>T</code> are excluded.
	 * Be careful when adding classes to <code>stdlib</code>. 
	 *
	 * Exclude patterns must include
	 * 
	 * <pre>
	 *  "*$$Lambda$*" "java.*" "jdk.*" "sun.*"
	 * </pre>
	 * 
	 * otherwise the Trace code itself will fail to run.
	 */
	public static void addExcludePattern (String value) {
		EXCLUDE_GLOBS.add (value);
	}
	/**
	 * Remove an exclude pattern.
	 *
	 * @see addExcludePattern
	 */
	public static void removeExcludePattern (String value) {
		EXCLUDE_GLOBS.remove (value);
	}
	protected static HashSet<String> EXCLUDE_GLOBS;
	static {
		EXCLUDE_GLOBS = new HashSet<> ();
		EXCLUDE_GLOBS.add ("*$$Lambda$*");
		EXCLUDE_GLOBS.add ("java.*");
		EXCLUDE_GLOBS.add ("jdk.*");
		EXCLUDE_GLOBS.add ("sun.*");
		EXCLUDE_GLOBS.add ("com.*");
		EXCLUDE_GLOBS.add ("org.*");
		EXCLUDE_GLOBS.add ("javax.*");
		EXCLUDE_GLOBS.add ("apple.*");
		EXCLUDE_GLOBS.add ("Jama.*");
		EXCLUDE_GLOBS.add ("qs.*");
		EXCLUDE_GLOBS.add ("stdlib.A*");
		EXCLUDE_GLOBS.add ("stdlib.B*");
		EXCLUDE_GLOBS.add ("stdlib.C*");
		EXCLUDE_GLOBS.add ("stdlib.D*");
		EXCLUDE_GLOBS.add ("stdlib.E*");
		EXCLUDE_GLOBS.add ("stdlib.F*");
		EXCLUDE_GLOBS.add ("stdlib.G*");
		EXCLUDE_GLOBS.add ("stdlib.H*");
		EXCLUDE_GLOBS.add ("stdlib.I*");
		EXCLUDE_GLOBS.add ("stdlib.J*");
		EXCLUDE_GLOBS.add ("stdlib.K*");
		EXCLUDE_GLOBS.add ("stdlib.L*");
		EXCLUDE_GLOBS.add ("stdlib.M*");
		EXCLUDE_GLOBS.add ("stdlib.N*");
		EXCLUDE_GLOBS.add ("stdlib.O*");
		EXCLUDE_GLOBS.add ("stdlib.P*");
		EXCLUDE_GLOBS.add ("stdlib.Q*");
		EXCLUDE_GLOBS.add ("stdlib.R*");
		EXCLUDE_GLOBS.add ("stdlib.S*");
		EXCLUDE_GLOBS.add ("stdlib.U*");
		EXCLUDE_GLOBS.add ("stdlib.V*");
		EXCLUDE_GLOBS.add ("stdlib.W*");
		EXCLUDE_GLOBS.add ("stdlib.X*");
		EXCLUDE_GLOBS.add ("stdlib.Y*");
		//EXCLUDE_GLOBS.add ("stdlib.Z*");	
	}
	/**
	 * Add an include pattern for drawing.  These are classes that should be shown in drawing and console logs, which would otherwise be excluded.
	 * The default is:
	 *
	 * <pre>
	 *  "java.util.*"
	 * </pre>
	 */
	public static void addDrawingIncludePattern (String value) {
		DRAWING_INCLUDE_GLOBS.add (value);
	}
	/**
	 * Add an include pattern.
	 *
	 * @see addDrawingIncludePattern
	 */
	public static void removeDrawingIncludePattern (String value) {
		DRAWING_INCLUDE_GLOBS.remove (value);
	}
	protected static HashSet<String> DRAWING_INCLUDE_GLOBS;
	static {
		DRAWING_INCLUDE_GLOBS = new HashSet<> ();
		DRAWING_INCLUDE_GLOBS.add ("java.util.*");
	}
	/**
	 * When the debugged program ends, create a graphviz file showing the call tree (default==false).
	 */
	public static void drawCallTree (boolean value) { SHOW_CALL_TREE = value; }
	protected static boolean SHOW_CALL_TREE = false;
	/**
	 * Graphviz style for a call tree node.
	 */
	public static void graphvizCallTreeBoxAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_ARRAY_BOX_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_CALL_TREE_BOX_ATTRIBUTES = ",shape=record";
	/**
	 * Graphviz style for a call tree arrow.
	 */
	public static void graphvizCallTreeArrowAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_ARRAY_ARROW_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_CALL_TREE_ARROW_ATTRIBUTES = ",fontsize=12";
	/**
	 * Graphviz style for an array.
	 */
	public static void graphvizArrayBoxAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_ARRAY_BOX_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_ARRAY_BOX_ATTRIBUTES = ",shape=record,color=blue";
	/**
	 * Graphviz style for an arrow from an array to an Object.
	 */
	public static void graphvizArrayArrowAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_ARRAY_ARROW_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_ARRAY_ARROW_ATTRIBUTES = ",fontsize=12,color=blue,arrowtail=dot,dir=both,tailclip=false";
	/**
	 * Graphviz style for a frame.
	 */
	public static void graphvizFrameBoxAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_FRAME_BOX_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_FRAME_BOX_ATTRIBUTES = ",shape=record,color=red";
	/**
	 * Graphviz style for an arrow from a frame to an Object.
	 */
	public static void graphvizFrameObjectArrowAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_FRAME_OBJECT_ARROW_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_FRAME_OBJECT_ARROW_ATTRIBUTES = ",fontsize=12,color=red";
	/**
	 * Graphviz style for an arrow from a return value to a frame.
	 */
	public static void graphvizFrameReturnAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_FRAME_RETURN_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_FRAME_RETURN_ATTRIBUTES = ",color=red";
	/**
	 * Graphviz style for an arrow from an exception to a frame.
	 */
	public static void graphvizFrameExceptionAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_FRAME_EXCEPTION_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_FRAME_EXCEPTION_ATTRIBUTES = ",color=red";
	/**
	 * Graphviz style for an arrow from a frame to another frame.
	 */
	public static void graphvizFrameFrameArrowAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_FRAME_FRAME_ARROW_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_FRAME_FRAME_ARROW_ATTRIBUTES = ",color=red,style=dashed";
	/**
	 * Graphviz style for an object (non-array).
	 */
	public static void graphvizObjectBoxAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_OBJECT_BOX_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_OBJECT_BOX_ATTRIBUTES = ",shape=record,color=purple";
	/**
	 * Graphviz style for a wrapper object (in simple form).
	 */
	public static void graphvizWrapperBoxAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_WRAPPER_BOX_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_WRAPPER_BOX_ATTRIBUTES = ",shape=ellipse,color=purple";
	/**
	 * Graphviz style for an arrow from an object to an object.
	 */
	public static void graphvizObjectArrowAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_OBJECT_ARROW_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_OBJECT_ARROW_ATTRIBUTES = ",fontsize=12,color=purple";
	/**
	 * Graphviz style for a static class.
	 */
	public static void graphvizStaticClassBoxAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_STATIC_CLASS_BOX_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_STATIC_CLASS_BOX_ATTRIBUTES = ",shape=record,color=orange";
	/**
	 * Graphviz style for an arrow from a static class to an object.
	 */
	public static void graphvizStaticClassArrowAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_STATIC_CLASS_ARROW_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_STATIC_CLASS_ARROW_ATTRIBUTES = ",fontsize=12,color=orange";
	/**
	 * Graphviz style for box labels.
	 */
	public static void graphvizLabelBoxAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_LABEL_BOX_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_LABEL_BOX_ATTRIBUTES = ",shape=none,color=black";
	/**
	 * Graphviz style for arrow labels.
	 */
	public static void graphvizLabelArrowAttributes (String value) {
		String v = (value == null || "".equals (value)) ? "" : "," + value;
		GRAPHVIZ_LABEL_ARROW_ATTRIBUTES = v;
	}
	protected static String GRAPHVIZ_LABEL_ARROW_ATTRIBUTES = ",color=black";

	// Graphiz execution
	/**
	 * Add a filesystem location to search for the dot executable that comes
	 * with graphviz. The default is system dependent.
	 */
	public static void graphvizAddPossibleDotLocation (String value) {
		GRAPHVIZ_POSSIBLE_DOT_LOCATIONS.add (value);
	}
	protected static ArrayList<String> GRAPHVIZ_POSSIBLE_DOT_LOCATIONS;
	static {
		GRAPHVIZ_POSSIBLE_DOT_LOCATIONS = new ArrayList<> ();
		String os = System.getProperty ("os.name").toLowerCase ();
		if (os.startsWith ("win")) {
			GRAPHVIZ_POSSIBLE_DOT_LOCATIONS.add ("c:/Program Files (x86)/Graphviz2.38/bin/dot.exe");
			GRAPHVIZ_POSSIBLE_DOT_LOCATIONS.add (System.getProperty ("user.dir") + "/lib/graphviz-windows/bin/dot.exe");
		} else if (os.startsWith ("mac")) {
			GRAPHVIZ_POSSIBLE_DOT_LOCATIONS.add ("/usr/local/bin/dot");
			GRAPHVIZ_POSSIBLE_DOT_LOCATIONS.add ("/usr/bin/dot");
			GRAPHVIZ_POSSIBLE_DOT_LOCATIONS.add (System.getProperty ("user.dir") + "/lib/graphviz-mac/bin/dot");
		} else {
			GRAPHVIZ_POSSIBLE_DOT_LOCATIONS.add ("/usr/local/bin/dot");
			GRAPHVIZ_POSSIBLE_DOT_LOCATIONS.add ("/usr/bin/dot");
		}
	}
	/**
	 * Remove graphviz files for which graphic files have been successfully
	 * generated.
	 */
	public static void graphvizRemoveGvFiles (boolean value) {
		GRAPHVIZ_REMOVE_GV_FILES = value;
	}
	protected static boolean GRAPHVIZ_REMOVE_GV_FILES = true;

	/**
	 * Show fully qualified class names (default==false). If
	 * showPackageInClassName is true, then showOuterClassInClassName is ignored
	 * (taken to be true).
	 */
	public static void showPackageInClassName (boolean value) {
		SHOW_PACKAGE_IN_CLASS_NAME = value;
	}
	protected static boolean SHOW_PACKAGE_IN_CLASS_NAME = false;
	/**
	 * Include fully qualified class names (default==false).
	 */
	public static void showOuterClassInClassName (boolean value) {
		SHOW_OUTER_CLASS_IN_CLASS_NAME = value;
	}
	protected static boolean SHOW_OUTER_CLASS_IN_CLASS_NAME = false;
	/**
	 * Show the object type in addition to its id (default==false).
	 */
	public static void consoleShowTypeInObjectName (boolean value) {
		CONSOLE_SHOW_TYPE_IN_OBJECT_NAME = value;
	}
	protected static boolean CONSOLE_SHOW_TYPE_IN_OBJECT_NAME = false;
	/**
	 * The maximum number of displayed fields when printing an object on the
	 * console (default==8).
	 */
	public static void consoleMaxFields (int value) {
		CONSOLE_MAX_FIELDS = value;
	}
	protected static int CONSOLE_MAX_FIELDS = 8;
	/**
	 * The maximum number of displayed elements when printing a primitive array
	 * on the console (default==15).
	 */
	public static void consoleMaxArrayElementsPrimitive (int value) {
		CONSOLE_MAX_ARRAY_ELEMENTS_PRIMITIVE = value;
	}
	protected static int CONSOLE_MAX_ARRAY_ELEMENTS_PRIMITIVE = 15;
	/**
	 * The maximum number of displayed elements when printing an object array on
	 * the console (default==8).
	 */
	public static void consoleMaxArrayElementsObject (int value) {
		CONSOLE_MAX_ARRAY_ELEMENTS_OBJECT = value;
	}
	protected static int CONSOLE_MAX_ARRAY_ELEMENTS_OBJECT = 8;
	/**
	 * Show object ids inside multidimensional arrays (default==false).
	 */
	public static void consoleShowNestedArrayIds (boolean value) {
		CONSOLE_SHOW_NESTED_ARRAY_IDS = value;
	}
	protected static boolean CONSOLE_SHOW_NESTED_ARRAY_IDS = false;
	/**
	 * Show fields introduced by the compiler (default==true);
	 */
	public static void showSyntheticFields (boolean value) {
		SHOW_SYNTHETIC_FIELDS = value;
	}
	protected static boolean SHOW_SYNTHETIC_FIELDS = true;
	/**
	 * Show methods introduced by the compiler (default==true);
	 */
	public static void showSyntheticMethods (boolean value) {
		SHOW_SYNTHETIC_METHODS = value;
	}
	protected static boolean SHOW_SYNTHETIC_METHODS = true;
	/**
	 * Show file names on the console (default==false).
	 */
	public static void showFilenamesOnConsole (boolean value) {
		GRAPHVIZ_SHOW_FILENAMES_ON_CONSOLE = value;
	}
	protected static boolean GRAPHVIZ_SHOW_FILENAMES_ON_CONSOLE = false;
	/**
	 * In graphviz, show field name in the label of an object (default==true).
	 */
	public static void showFieldNamesInLabels (boolean value) {
		GRAPHVIZ_SHOW_FIELD_NAMES_IN_LABELS = value;
	}
	protected static boolean GRAPHVIZ_SHOW_FIELD_NAMES_IN_LABELS = true;
	/**
	 * In graphviz, show object ids (default==false).
	 */
	public static void showObjectIds (boolean value) {
		GRAPHVIZ_SHOW_OBJECT_IDS = value;
	}
	protected static boolean GRAPHVIZ_SHOW_OBJECT_IDS = false;
	/**
	 * In graphviz, show object ids and redundant variables for arrows (default==false).
	 * This also sets showBuiltInObjects to value.
	 */
	public static void showObjectIdsRedundantly (boolean value) {
		SHOW_STRINGS_AS_PRIMITIVE = !value;
		SHOW_BOXED_PRIMITIVES_AS_PRIMITIVE = !value;
		GRAPHVIZ_SHOW_OBJECT_IDS = value;
		GRAPHVIZ_SHOW_OBJECT_IDS_REDUNDANTLY = value;
	}
	protected static boolean GRAPHVIZ_SHOW_OBJECT_IDS_REDUNDANTLY = false;
	/**
	 * In graphviz, show stack frame numbers (default==true).
	 */
	public static void showFrameNumbers (boolean value) {
		GRAPHVIZ_SHOW_FRAME_NUMBERS = value;
	}
	protected static boolean GRAPHVIZ_SHOW_FRAME_NUMBERS = true;
	/**
	 * In graphviz, show null fields (default==true).
	 */
	public static void showNullFields (boolean value) {
		GRAPHVIZ_SHOW_NULL_FIELDS = value;
	}
	protected static boolean GRAPHVIZ_SHOW_NULL_FIELDS = true;
	/**
	 * In graphviz, show null variabels (default==true).
	 */
	public static void showNullVariables (boolean value) {
		GRAPHVIZ_SHOW_NULL_VARIABLES = value;
	}
	protected static boolean GRAPHVIZ_SHOW_NULL_VARIABLES = true;
	/**
	 * Include line number in graphviz filename (default==true).
	 */
	public static void graphvizPutLineNumberInFilename (boolean value) {
		GRAPHVIZ_PUT_LINE_NUMBER_IN_FILENAME = value;
	}
	protected static boolean GRAPHVIZ_PUT_LINE_NUMBER_IN_FILENAME = true;
	/**
	 * Do not display any fields with this name (default includes only
	 * "$assertionsDisabled").
	 */
	public static void addGraphvizIgnoredFields (String value) {
		GRAPHVIZ_IGNORED_FIELDS.add (value);
	}
	protected static ArrayList<String> GRAPHVIZ_IGNORED_FIELDS;
	static {
		GRAPHVIZ_IGNORED_FIELDS = new ArrayList<> ();
		GRAPHVIZ_IGNORED_FIELDS.add ("$assertionsDisabled");
	}
	/**
	 * Set the graphviz attributes for objects of the given class.
	 */
	public static void graphvizSetObjectAttribute (Class<?> cz, String attrib) {
		Graphviz.objectAttributeMap.put (cz.getName (), attrib);
	}
	/**
	 * Set the graphviz attributes for objects of the given class.
	 */
	public static void graphvizSetStaticClassAttribute (Class<?> cz, String attrib) {
		Graphviz.staticClassAttributeMap.put (cz.getName (), attrib);
	}
	/**
	 * Set the graphviz attributes for frames of the given class.
	 */
	public static void graphvizSetFrameAttribute (Class<?> cz, String attrib) {
		Graphviz.frameAttributeMap.put (cz.getName (), attrib);
	}
	/**
	 * Set the graphviz attributes for all fields with the given name.
	 */
	public static void graphvizSetFieldAttribute (String field, String attrib) {
		Graphviz.fieldAttributeMap.put (field, attrib);
	}
	protected static String BAD_ERROR_MESSAGE = "\n!!!! This shouldn't happen! \n!!!! Please contact your instructor or the author of " + Trace.class.getCanonicalName ();

	// ---------------------- Launch the JVM  ----------------------------------

	// Set up a launching connection to the JVM
	private static VirtualMachine launchConnect (String[] args) {
		VirtualMachine vm = null;
		LaunchingConnector conn = getCommandLineConnector ();
		Map<String, Connector.Argument> connArgs = setMainArgs (conn, args);

		try {
			vm = conn.launch (connArgs); // launch the JVM and connect to it
		} catch (IOException e) {
			throw new Error ("\n!!!! Unable to launch JVM: " + e);
		} catch (IllegalConnectorArgumentsException e) {
			throw new Error ("\n!!!! Internal error: " + e);
		} catch (VMStartException e) {
			throw new Error ("\n!!!! JVM failed to start: " + e.getMessage ());
		}

		return vm;
	}

	// find a command line launch connector
	private static LaunchingConnector getCommandLineConnector () {
		List<Connector> conns = Bootstrap.virtualMachineManager ().allConnectors ();

		for (Connector conn : conns) {
			if (conn.name ().equals ("com.sun.jdi.CommandLineLaunch")) return (LaunchingConnector) conn;
		}
		throw new Error ("\n!!!! No launching connector found");
	}

	// make the tracer's input arguments the program's main() arguments
	private static Map<String, Connector.Argument> setMainArgs (LaunchingConnector conn, String[] args) {
		// get the connector argument for the program's main() method
		Map<String, Connector.Argument> connArgs = conn.defaultArguments ();
		Connector.Argument mArgs = connArgs.get ("main");
		if (mArgs == null) throw new Error ("\n!!!! Bad launching connector");

		// concatenate all the tracer's input arguments into a single string
		StringBuffer sb = new StringBuffer ();
		for (int i = 0; i < args.length; i++)
			sb.append (args[i] + " ");

		mArgs.setValue (sb.toString ()); // assign input args to application's main()
		return connArgs;
	}

	// monitor the JVM running the application
	private static void monitorJVM (VirtualMachine vm) {
		// start JDI event handler which displays trace info
		JDIEventMonitor watcher = new JDIEventMonitor (vm);
		watcher.start ();

		// redirect VM's output and error streams to the system output and error streams
		Process process = vm.process ();
		Thread errRedirect = new StreamRedirecter ("error reader", process.getErrorStream (), System.err);
		Thread outRedirect = new StreamRedirecter ("output reader", process.getInputStream (), System.out);
		errRedirect.start ();
		outRedirect.start ();

		vm.resume (); // start the application

		try {
			watcher.join (); // Wait. Shutdown begins when the JDI watcher terminates
			errRedirect.join (); // make sure all the stream outputs have been forwarded before we exit
			outRedirect.join ();
		} catch (InterruptedException e) {}
	}
}

/**
 * StreamRedirecter is a thread which copies it's input to it's output and
 * terminates when it completes.
 *
 * @author Robert Field, September 2005
 * @author Andrew Davison, March 2009, ad@fivedots.coe.psu.ac.th
 */
/* private static */class StreamRedirecter extends Thread {
	private static final int BUFFER_SIZE = 2048;
	private final Reader in;
	private final Writer out;

	public StreamRedirecter (String name, InputStream in, OutputStream out) {
		super (name);
		this.in = new InputStreamReader (in); // stream to copy from
		this.out = new OutputStreamWriter (out); // stream to copy to
		setPriority (Thread.MAX_PRIORITY - 1);
	}

	// copy BUFFER_SIZE chars at a time
	public void run () {
		try {
			char[] cbuf = new char[BUFFER_SIZE];
			int count;
			while ((count = in.read (cbuf, 0, BUFFER_SIZE)) >= 0)
				out.write (cbuf, 0, count);
			out.flush ();
		} catch (IOException e) {
			System.err.println ("StreamRedirecter: " + e);
		}
	}

}

/**
 * Monitor incoming JDI events for a program running in the JVM and print out
 * trace/debugging information.
 *
 * This is a simplified version of EventThread.java from the Trace.java example
 * in the demo/jpda/examples.jar file in the JDK.
 *
 * Andrew Davison: The main addition is the use of the ShowCodes and ShowLines
 * classes to list the line being currently executed.
 *
 * James Riely: See comments in class Trace.
 *
 * @author Robert Field and Minoru Terada, September 2005
 * @author Iman_S, June 2008
 * @author Andrew Davison, ad@fivedots.coe.psu.ac.th, March 2009
 * @author James Riely, jriely@cs.depaul.edu, August 2014
 */
/* private static */class JDIEventMonitor extends Thread {
	// exclude events generated for these classes
	private final VirtualMachine vm; // the JVM
	private boolean connected = true; // connected to VM?
	private boolean vmDied; // has VM death occurred?
	private final JDIEventHandler printer = new Printer ();

	public JDIEventMonitor (VirtualMachine jvm) {
		super ("JDIEventMonitor");
		vm = jvm;
		setEventRequests ();
	}

	// Create and enable the event requests for the events we want to monitor in
	// the running program.
	//
	// Created here:
	//
	//    createThreadStartRequest()
	//    createThreadDeathRequest()
	//    createClassPrepareRequest()
	//    createClassUnloadRequest()
	//    createMethodEntryRequest()
	//    createMethodExitRequest()
	//    createExceptionRequest(ReferenceType refType, boolean notifyCaught, boolean notifyUncaught)
	//    createMonitorContendedEnterRequest()
	//    createMonitorContendedEnteredRequest()
	//    createMonitorWaitRequest()
	//    createMonitorWaitedRequest()
	//
	// Created when class is loaded:
	//
	//    createModificationWatchpointRequest(Field field)
	//
	// Created when thread is started:
	//
	//    createStepRequest(ThreadReference thread, int size, int depth)
	//
	// Unused:
	//
	//    createAccessWatchpointRequest(Field field)
	//    createBreakpointRequest(Location location)
	//
	// Unnecessary:
	//
	//    createVMDeathRequest() // these happen even without being requested
	//
	private void setEventRequests () {
		EventRequestManager mgr = vm.eventRequestManager ();
		{
			ThreadStartRequest x = mgr.createThreadStartRequest (); // report thread starts
			x.enable ();
		}
		{
			ThreadDeathRequest x = mgr.createThreadDeathRequest (); // report thread deaths
			x.enable ();
		}
		{
			ClassPrepareRequest x = mgr.createClassPrepareRequest (); // report class loads
			for (String s : Trace.EXCLUDE_GLOBS)
				x.addClassExclusionFilter (s);
			// x.setSuspendPolicy(EventRequest.SUSPEND_ALL);
			x.enable ();
		}
		{
			ClassUnloadRequest x = mgr.createClassUnloadRequest (); // report class unloads
			for (String s : Trace.EXCLUDE_GLOBS)
				x.addClassExclusionFilter (s);
			// x.setSuspendPolicy(EventRequest.SUSPEND_ALL);
			x.enable ();
		}
		{
			MethodEntryRequest x = mgr.createMethodEntryRequest (); // report method entries
			for (String s : Trace.EXCLUDE_GLOBS)
				x.addClassExclusionFilter (s);
			x.setSuspendPolicy (EventRequest.SUSPEND_EVENT_THREAD);
			x.enable ();
		}
		{
			MethodExitRequest x = mgr.createMethodExitRequest (); // report method exits
			for (String s : Trace.EXCLUDE_GLOBS)
				x.addClassExclusionFilter (s);
			x.setSuspendPolicy (EventRequest.SUSPEND_EVENT_THREAD);
			x.enable ();
		}
		{
			ExceptionRequest x = mgr.createExceptionRequest (null, true, true); // report all exceptions, caught and uncaught
			for (String s : Trace.EXCLUDE_GLOBS)
				x.addClassExclusionFilter (s);
			x.setSuspendPolicy (EventRequest.SUSPEND_EVENT_THREAD);
			x.enable ();
		}
		{
			MonitorContendedEnterRequest x = mgr.createMonitorContendedEnterRequest ();
			for (String s : Trace.EXCLUDE_GLOBS)
				x.addClassExclusionFilter (s);
			x.setSuspendPolicy (EventRequest.SUSPEND_EVENT_THREAD);
			x.enable ();
		}
		{
			MonitorContendedEnteredRequest x = mgr.createMonitorContendedEnteredRequest ();
			for (String s : Trace.EXCLUDE_GLOBS)
				x.addClassExclusionFilter (s);
			x.setSuspendPolicy (EventRequest.SUSPEND_EVENT_THREAD);
			x.enable ();
		}
		{
			MonitorWaitRequest x = mgr.createMonitorWaitRequest ();
			for (String s : Trace.EXCLUDE_GLOBS)
				x.addClassExclusionFilter (s);
			x.setSuspendPolicy (EventRequest.SUSPEND_EVENT_THREAD);
			x.enable ();
		}
		{
			MonitorWaitedRequest x = mgr.createMonitorWaitedRequest ();
			for (String s : Trace.EXCLUDE_GLOBS)
				x.addClassExclusionFilter (s);
			x.setSuspendPolicy (EventRequest.SUSPEND_EVENT_THREAD);
			x.enable ();
		}
	}

	// process JDI events as they arrive on the event queue
	public void run () {
		EventQueue queue = vm.eventQueue ();
		while (connected) {
			try {
				EventSet eventSet = queue.remove ();
				for (Event event : eventSet)
					handleEvent (event);
				eventSet.resume ();
			} catch (InterruptedException e) {
				// Ignore
			} catch (VMDisconnectedException discExc) {
				handleDisconnectedException ();
				break;
			}
		}
		printer.printCallTree ();
	}

	// process a JDI event
	private void handleEvent (Event event) {
		if (Trace.DEBUG) System.err.print (event.getClass ().getSimpleName ().replace ("EventImpl", ""));

		// step event -- a line of code is about to be executed
		if (event instanceof StepEvent) {
			stepEvent ((StepEvent) event);
			return;
		}

		// modified field event  -- a field is about to be changed
		if (event instanceof ModificationWatchpointEvent) {
			modificationWatchpointEvent ((ModificationWatchpointEvent) event);
			return;
		}

		// method events
		if (event instanceof MethodEntryEvent) {
			methodEntryEvent ((MethodEntryEvent) event);
			return;
		}
		if (event instanceof MethodExitEvent) {
			methodExitEvent ((MethodExitEvent) event);
			return;
		}
		if (event instanceof ExceptionEvent) {
			exceptionEvent ((ExceptionEvent) event);
			return;
		}

		// monitor events
		if (event instanceof MonitorContendedEnterEvent) {
			monitorContendedEnterEvent ((MonitorContendedEnterEvent) event);
			return;
		}
		if (event instanceof MonitorContendedEnteredEvent) {
			monitorContendedEnteredEvent ((MonitorContendedEnteredEvent) event);
			return;
		}
		if (event instanceof MonitorWaitEvent) {
			monitorWaitEvent ((MonitorWaitEvent) event);
			return;
		}
		if (event instanceof MonitorWaitedEvent) {
			monitorWaitedEvent ((MonitorWaitedEvent) event);
			return;
		}

		// class events
		if (event instanceof ClassPrepareEvent) {
			classPrepareEvent ((ClassPrepareEvent) event);
			return;
		}
		if (event instanceof ClassUnloadEvent) {
			classUnloadEvent ((ClassUnloadEvent) event);
			return;
		}

		// thread events
		if (event instanceof ThreadStartEvent) {
			threadStartEvent ((ThreadStartEvent) event);
			return;
		}
		if (event instanceof ThreadDeathEvent) {
			threadDeathEvent ((ThreadDeathEvent) event);
			return;
		}

		// VM events
		if (event instanceof VMStartEvent) {
			vmStartEvent ((VMStartEvent) event);
			return;
		}
		if (event instanceof VMDeathEvent) {
			vmDeathEvent ((VMDeathEvent) event);
			return;
		}
		if (event instanceof VMDisconnectEvent) {
			vmDisconnectEvent ((VMDisconnectEvent) event);
			return;
		}

		throw new Error ("\n!!!! Unexpected event type: " + event.getClass ().getCanonicalName ());
	}

	// A VMDisconnectedException has occurred while dealing with another event.
	// Flush the event queue, dealing only with exit events (VMDeath,
	// VMDisconnect) so that things terminate correctly.
	private synchronized void handleDisconnectedException () {
		EventQueue queue = vm.eventQueue ();
		while (connected) {
			try {
				EventSet eventSet = queue.remove ();
				for (Event event : eventSet) {
					if (event instanceof VMDeathEvent) vmDeathEvent ((VMDeathEvent) event);
					else if (event instanceof VMDisconnectEvent) vmDisconnectEvent ((VMDisconnectEvent) event);
				}
				eventSet.resume (); // resume the VM
			} catch (InterruptedException e) {
				// ignore
			} catch (VMDisconnectedException e) {
				// ignore
			}
		}
	}

	// ---------------------- VM event handling ----------------------------------

	// Notification of initialization of a target VM. This event is received
	// before the main thread is started and before any application code has
	// been executed.
	private void vmStartEvent (VMStartEvent event) {
		vmDied = false;
		printer.vmStartEvent (event);
	}

	// Notification of VM termination
	private void vmDeathEvent (VMDeathEvent event) {
		vmDied = true;
		printer.vmDeathEvent (event);
	}

	// Notification of disconnection from the VM, either through normal
	// termination or because of an exception/error.
	private void vmDisconnectEvent (VMDisconnectEvent event) {
		connected = false;
		if (!vmDied) printer.vmDisconnectEvent (event);
	}

	// -------------------- class event handling  ---------------

	// a new class has been loaded
	private void classPrepareEvent (ClassPrepareEvent event) {
		ReferenceType type = event.referenceType ();
		String typeName = type.name ();
		if (Trace.CALLBACK_CLASS_NAME.equals (typeName) || Trace.GRAPHVIZ_CLASS_NAME.equals (typeName)) return;
		List<Field> fields = type.fields ();

		// register field modification events
		EventRequestManager mgr = vm.eventRequestManager ();
		for (Field field : fields) {
			ModificationWatchpointRequest req = mgr.createModificationWatchpointRequest (field);
			for (String s : Trace.EXCLUDE_GLOBS)
				req.addClassExclusionFilter (s);
			req.setSuspendPolicy (EventRequest.SUSPEND_NONE);
			req.enable ();
		}
		printer.classPrepareEvent (event);

	}
	// a class has been unloaded
	private void classUnloadEvent (ClassUnloadEvent event) {
		if (!vmDied) printer.classUnloadEvent (event);
	}

	// -------------------- thread event handling  ---------------

	// a new thread has started running -- switch on single stepping
	private void threadStartEvent (ThreadStartEvent event) {
		ThreadReference thr = event.thread ();
		if (Format.ignoreThread (thr)) return;
		EventRequestManager mgr = vm.eventRequestManager ();

		StepRequest sr = mgr.createStepRequest (thr, StepRequest.STEP_LINE, StepRequest.STEP_INTO);
		sr.setSuspendPolicy (EventRequest.SUSPEND_EVENT_THREAD);

		for (String s : Trace.EXCLUDE_GLOBS)
			sr.addClassExclusionFilter (s);
		sr.enable ();
		printer.threadStartEvent (event);
	}

	// the thread is about to terminate
	private void threadDeathEvent (ThreadDeathEvent event) {
		ThreadReference thr = event.thread ();
		if (Format.ignoreThread (thr)) return;
		printer.threadDeathEvent (event);
	}

	// -------------------- delegated --------------------------------

	private void methodEntryEvent (MethodEntryEvent event) {
		printer.methodEntryEvent (event);
	}
	private void methodExitEvent (MethodExitEvent event) {
		printer.methodExitEvent (event);
	}
	private void exceptionEvent (ExceptionEvent event) {
		printer.exceptionEvent (event);
	}
	private void stepEvent (StepEvent event) {
		printer.stepEvent (event);
	}
	private void modificationWatchpointEvent (ModificationWatchpointEvent event) {
		printer.modificationWatchpointEvent (event);
	}
	private void monitorContendedEnterEvent (MonitorContendedEnterEvent event) {
		printer.monitorContendedEnterEvent (event);
	}
	private void monitorContendedEnteredEvent (MonitorContendedEnteredEvent event) {
		printer.monitorContendedEnteredEvent (event);
	}
	private void monitorWaitEvent (MonitorWaitEvent event) {
		printer.monitorWaitEvent (event);
	}
	private void monitorWaitedEvent (MonitorWaitedEvent event) {
		printer.monitorWaitedEvent (event);
	}
}

/**
 * Printer for events. Prints and updates the ValueMap. Handles Graphviz drawing
 * requests.
 *
 * @author James Riely, jriely@cs.depaul.edu, August 2014
 */
/* private static */interface IndentPrinter {
	public void println(ThreadReference thr, String string);
}

/* private static */interface JDIEventHandler {
	public void printCallTree();
	/** Notification of target VM termination. */
	public void vmDeathEvent(VMDeathEvent event);
	/** Notification of disconnection from target VM. */
	public void vmDisconnectEvent(VMDisconnectEvent event);
	/** Notification of initialization of a target VM. */
	public void vmStartEvent(VMStartEvent event);
	/** Notification of a new running thread in the target VM. */
	public void threadStartEvent(ThreadStartEvent event);
	/** Notification of a completed thread in the target VM. */
	public void threadDeathEvent(ThreadDeathEvent event);
	/** Notification of a class prepare in the target VM. */
	public void classPrepareEvent(ClassPrepareEvent event);
	/** Notification of a class unload in the target VM. */
	public void classUnloadEvent(ClassUnloadEvent event);
	/** Notification of a field access in the target VM. */
	//public void accessWatchpointEvent (AccessWatchpointEvent event);
	/** Notification of a field modification in the target VM. */
	public void modificationWatchpointEvent(ModificationWatchpointEvent event);
	/** Notification of a method invocation in the target VM. */
	public void methodEntryEvent(MethodEntryEvent event);
	/** Notification of a method return in the target VM. */
	public void methodExitEvent(MethodExitEvent event);
	/** Notification of an exception in the target VM. */
	public void exceptionEvent(ExceptionEvent event);
	/** Notification of step completion in the target VM. */
	public void stepEvent(StepEvent event);
	/** Notification of a breakpoint in the target VM. */
	//public void breakpointEvent (BreakpointEvent event);
	/**
	 * Notification that a thread in the target VM is attempting to enter a
	 * monitor that is already acquired by another thread.
	 */
	public void monitorContendedEnterEvent(MonitorContendedEnterEvent event);
	/**
	 * Notification that a thread in the target VM is entering a monitor after
	 * waiting for it to be released by another thread.
	 */
	public void monitorContendedEnteredEvent(MonitorContendedEnteredEvent event);
	/**
	 * Notification that a thread in the target VM is about to wait on a monitor
	 * object.
	 */
	public void monitorWaitEvent(MonitorWaitEvent event);
	/**
	 * Notification that a thread in the target VM has finished waiting on an
	 * monitor object.
	 */
	public void monitorWaitedEvent(MonitorWaitedEvent event);
}

/* private static */class Printer implements IndentPrinter, JDIEventHandler {
	private final Set<ReferenceType> staticClasses = new HashSet<> ();
	private final Map<ThreadReference, Value> returnValues = new HashMap<> ();
	private final Map<ThreadReference, Value> exceptionsMap = new HashMap<> ();
	private final ValueMap values = new ValueMap ();
	private final CodeMap codeMap = new CodeMap ();
	private final InsideIgnoredMethodMap boolMap = new InsideIgnoredMethodMap ();

	public void monitorContendedEnterEvent (MonitorContendedEnterEvent event) {}
	public void monitorContendedEnteredEvent (MonitorContendedEnteredEvent event) {}
	public void monitorWaitEvent (MonitorWaitEvent event) {}
	public void monitorWaitedEvent (MonitorWaitedEvent event) {}

	public void vmStartEvent (VMStartEvent event) {
		if (Trace.CONSOLE_SHOW_THREADS) println ("|||| VM Started");
	}
	public void vmDeathEvent (VMDeathEvent event) {
		if (Trace.CONSOLE_SHOW_THREADS) println ("|||| VM Stopped");
	}
	public void vmDisconnectEvent (VMDisconnectEvent event) {
		if (Trace.CONSOLE_SHOW_THREADS) println ("|||| VM Disconnected application");
	}
	public void threadStartEvent (ThreadStartEvent event) {
		ThreadReference thr = event.thread ();
		values.stackCreate (thr);
		boolMap.addThread (thr);
		if (Trace.CONSOLE_SHOW_THREADS) println ("|||| thread started: " + thr.name ());
	}
	public void threadDeathEvent (ThreadDeathEvent event) {
		ThreadReference thr = event.thread ();
		values.stackDestroy (thr);
		boolMap.removeThread (thr);
		if (Trace.CONSOLE_SHOW_THREADS) println ("|||| thread stopped: " + thr.name ());
	}
	public void classPrepareEvent (ClassPrepareEvent event) {

		ReferenceType ref = event.referenceType ();

		List<Field> fields = ref.fields ();
		List<Method> methods = ref.methods ();

		String filename;
		try {
			filename = ref.sourcePaths (null).get (0); // get filename of the class
			codeMap.addFile (filename);
		} catch (AbsentInformationException e) {
			filename = "??";
		}

		boolean hasConstructors = false;
		boolean hasObjectMethods = false;
		boolean hasClassMethods = false;
		boolean hasClassFields = false;
		boolean hasObjectFields = false;
		for (Method m : methods) {
			if (Format.isConstructor (m)) hasConstructors = true;
			if (Format.isObjectMethod (m)) hasObjectMethods = true;
			if (Format.isClassMethod (m)) hasClassMethods = true;
		}
		for (Field f : fields) {
			if (Format.isStaticField (f)) hasClassFields = true;
			if (Format.isObjectField (f)) hasObjectFields = true;
		}

		if (hasClassFields) {
			staticClasses.add (ref);
		}
		if (Trace.CONSOLE_SHOW_CLASSES) {
			println ("|||| loaded class: " + ref.name () + " from " + filename);
			if (hasClassFields) {
				println ("||||  class fields: ");
				for (Field f : fields)
					if (Format.isStaticField (f)) println ("||||    " + Format.fieldToString (f));
			}
			if (hasClassMethods) {
				println ("||||  class methods: ");
				for (Method m : methods)
					if (Format.isClassMethod (m)) println ("||||    " + Format.methodToString (m, false));
			}
			if (hasConstructors) {
				println ("||||  constructors: ");
				for (Method m : methods)
					if (Format.isConstructor (m)) println ("||||    " + Format.methodToString (m, false));
			}
			if (hasObjectFields) {
				println ("||||  object fields: ");
				for (Field f : fields)
					if (Format.isObjectField (f)) println ("||||    " + Format.fieldToString (f));
			}
			if (hasObjectMethods) {
				println ("||||  object methods: ");
				for (Method m : methods)
					if (Format.isObjectMethod (m)) println ("||||    " + Format.methodToString (m, false));
			}
		}
	}
	public void classUnloadEvent (ClassUnloadEvent event) {
		if (Trace.CONSOLE_SHOW_CLASSES) println ("|||| unloaded class: " + event.className ());
	}

	public void methodEntryEvent (MethodEntryEvent event) {
		Method meth = event.method ();
		ThreadReference thr = event.thread ();
		String calledMethodClassname = meth.declaringType ().name ();
		//System.err.println (calledMethodClassname);
		if (Format.matchesExcludePrefix (calledMethodClassname)) return;
		if (!Trace.SHOW_SYNTHETIC_METHODS && meth.isSynthetic ()) return;
		if (Trace.GRAPHVIZ_CLASS_NAME.equals (calledMethodClassname)) return;

		if (!Trace.CALLBACK_CLASS_NAME.equals (calledMethodClassname)) {
			StackFrame currFrame = Format.getFrame (meth, thr);
			values.stackPushFrame (currFrame, thr);
			if (Trace.CONSOLE_SHOW_STEPS || Trace.CONSOLE_SHOW_CALLS) {
				println (thr, ">>>> " + Format.methodToString (meth, true)); // + "[" + thr.name () + "]");
				printLocals (currFrame, thr);
			}
		} else {
		    // COPY PASTE HORRORS HERE
			boolMap.enteringIgnoredMethod (thr);
			String name = meth.name ();
			if (Trace.CALLBACK_CLEAR_CALL_TREE.equals (name)) {
				values.clearCallTree();
			} else if (Trace.CALLBACK_DRAW_STEPS_OF_METHOD.equals (name)) {
				List<StackFrame> frames;
				try {
					frames = thr.frames ();
				} catch (IncompatibleThreadStateException e) {
					throw new Error (Trace.BAD_ERROR_MESSAGE);
				}
				StackFrame currFrame = Format.getFrame (meth, thr);
				List<LocalVariable> locals;
				try {
					locals = currFrame.visibleVariables ();
				} catch (AbsentInformationException e) {
					return;
				}
				StringReference obj = (StringReference) currFrame.getValue (locals.get (0));
				Trace.drawStepsOfMethodBegin (obj.value());
				returnValues.put (thr, null);
			} else if (Trace.CALLBACK_DRAW_STEPS_OF_METHODS.equals (name)) {
				List<StackFrame> frames;
				try {
					frames = thr.frames ();
				} catch (IncompatibleThreadStateException e) {
					throw new Error (Trace.BAD_ERROR_MESSAGE);
				}
				StackFrame currFrame = Format.getFrame (meth, thr);
				List<LocalVariable> locals;
				try {
					locals = currFrame.visibleVariables ();
				} catch (AbsentInformationException e) {
					return;
				}
				ArrayReference arr = (ArrayReference) currFrame.getValue (locals.get (0)); 
				for (int i = arr.length() - 1; i >= 0; i--) {
					StringReference obj = (StringReference) arr.getValue (i);
					Trace.drawStepsOfMethodBegin (obj.value());
				}
				returnValues.put (thr, null);
			} else if (Trace.CALLBACK_DRAW_STEPS_BEGIN.equals (name)) {
				Trace.GRAPHVIZ_SHOW_STEPS = true;
				returnValues.put (thr, null);
			} else if (Trace.CALLBACK_DRAW_STEPS_END.equals (name)) {
				Trace.drawStepsOfMethodEnd ();
			} else if (Trace.CALLBACKS.contains (name)) {
				if (!Trace.GRAPHVIZ_SHOW_STEPS) {
					//System.err.println (calledMethodClassname + ":" + Trace.SPECIAL_METHOD_NAME + ":" + meth.name ());
					StackFrame frame;
					try {
						frame = thr.frame (1);
					} catch (IncompatibleThreadStateException e) {
						throw new Error (Trace.BAD_ERROR_MESSAGE);
					}
					Location loc = frame.location ();
					String label = Format.methodToString (loc.method (), true, false, "_") + "_" + Integer.toString (loc.lineNumber ());
					drawGraph (label, thr, meth);
					if (Trace.GRAPHVIZ_SHOW_FILENAMES_ON_CONSOLE && (Trace.CONSOLE_SHOW_STEPS)) printDrawEvent (thr, Graphviz.peekFilename ());
				}
			}
		}
	}
	public void methodExitEvent (MethodExitEvent event) {
		ThreadReference thr = event.thread ();
		Method meth = event.method ();
		String calledMethodClassname = meth.declaringType ().name ();
		if (Format.matchesExcludePrefix (calledMethodClassname)) return;
		if (!Trace.SHOW_SYNTHETIC_METHODS && meth.isSynthetic ()) return;
		if (Trace.GRAPHVIZ_CLASS_NAME.equals (calledMethodClassname)) return;
		if (boolMap.leavingIgnoredMethod (thr)) return;
		if (Trace.CONSOLE_SHOW_STEPS || Trace.CONSOLE_SHOW_CALLS) {
			Type returnType;
			try {
				returnType = meth.returnType ();
			} catch (ClassNotLoadedException e) {
				returnType = null;
			}
			if (returnType instanceof VoidType) {
				println (thr, "<<<< " + Format.methodToString (meth, true));
			} else {
				println (thr, "<<<< " + Format.methodToString (meth, true) + " : " + Format.valueToString (event.returnValue ()));
			}
		}
		values.stackPopFrame (thr);
		StackFrame currFrame = Format.getFrame (meth, thr);
		if (meth.isConstructor ()) {
			returnValues.put (thr, currFrame.thisObject ());
		} else {
			returnValues.put (thr, event.returnValue ());
		}
	}
	public void exceptionEvent (ExceptionEvent event) {
		ThreadReference thr = event.thread ();
		try {
			StackFrame currentFrame = thr.frame (0);
		} catch (IncompatibleThreadStateException e) {
			throw new Error (Trace.BAD_ERROR_MESSAGE);
		}
		ObjectReference exception = event.exception ();
		Location catchLocation = event.catchLocation ();
		//String name = Format.objectToStringLong (exception);
		String message = "()";
		Field messageField = exception.referenceType ().fieldByName ("detailMessage");
		if (messageField != null) {
			Value value = exception.getValue (messageField);
			if (value != null) {
				message = "(" + value.toString () + ")";
			}
		}
		String name = Format.shortenFullyQualifiedName (exception.referenceType ().name ()) + message;

		if (catchLocation == null) {
			// uncaught exception
			if (Trace.CONSOLE_SHOW_STEPS) println (thr, "!!!! UNCAUGHT EXCEPTION: " + name);
			if (Trace.GRAPHVIZ_SHOW_STEPS) Graphviz.drawFramesCheck (null, null, event.exception (), null, staticClasses);
		} else {
			if (Trace.CONSOLE_SHOW_STEPS) println (thr, "!!!! EXCEPTION: " + name);
			if (Trace.GRAPHVIZ_SHOW_STEPS) exceptionsMap.put (thr, event.exception ());
		}
	}
	public void stepEvent (StepEvent event) {
		ThreadReference thr = event.thread ();
		if (boolMap.insideIgnoredMethod (thr)) {
			//System.err.println ("ignored");
			return;
		}
		values.maybeAdjustAfterException (thr);

		Location loc = event.location ();
		String filename;
		try {
			filename = loc.sourcePath ();
		} catch (AbsentInformationException e) {
			return;
		}
		if (Trace.CONSOLE_SHOW_STEPS) {
			values.stackUpdateFrame (event.location ().method (), thr, this);
			int lineNumber = loc.lineNumber ();
			if (Trace.CONSOLE_SHOW_STEPS_VERBOSE) {
				println (thr, Format.shortenFilename (filename) + ":" + lineNumber + codeMap.show (filename, lineNumber));
			} else {
				printLineNum (thr, lineNumber);
			}
		}
		if (Trace.GRAPHVIZ_SHOW_STEPS) {
			try {
				Graphviz.drawFramesCheck (Format.methodToString (loc.method (), true, false, "_") + "_" + Integer.toString (loc.lineNumber ()), returnValues.get (thr),
						exceptionsMap.get (thr), thr.frames (), staticClasses);
				if (Trace.GRAPHVIZ_SHOW_FILENAMES_ON_CONSOLE && (Trace.CONSOLE_SHOW_STEPS)) printDrawEvent (thr, Graphviz.peekFilename ());
				returnValues.put (thr, null);
				exceptionsMap.put (thr, null);
			} catch (IncompatibleThreadStateException e) {
				throw new Error (Trace.BAD_ERROR_MESSAGE);
			}
		}

	}
	public void modificationWatchpointEvent (ModificationWatchpointEvent event) {
		ThreadReference thr = event.thread ();
		if (boolMap.insideIgnoredMethod (thr)) return;
		if (!Trace.CONSOLE_SHOW_STEPS) return;
		Field f = event.field ();
		Value value = event.valueToBe (); // value that _will_ be assigned
		String debug = Trace.DEBUG ? "#5" + "[" + thr.name () + "]" : "";
		Type type;
		try {
			type = f.type ();
		} catch (ClassNotLoadedException e) {
			type = null; // waiting for class to load
		}

		if (value instanceof ArrayReference) {
			if (f.isStatic ()) {
				String name = Format.shortenFullyQualifiedName (f.declaringType ().name ()) + "." + f.name ();
				if (values.registerStaticArray ((ArrayReference) value, name)) {
					println (thr, "  " + debug + "> " + name + " = " + Format.valueToString (value));
				}
			}
			return; // array types are handled separately -- this avoids redundant printing
		}
		ObjectReference objRef = event.object ();
		if (objRef == null) {
			println (thr, "  " + debug + "> " + Format.shortenFullyQualifiedName (f.declaringType ().name ()) + "." + f.name () + " = " + Format.valueToString (value));
		} else {
			// changes to array references are printed by updateFrame
			if (Format.tooManyFields (objRef)) {
				println (thr, "  " + debug + "> " + Format.objectToStringShort (objRef) + "." + f.name () + " = " + Format.valueToString (value));
			} else {
				println (thr, "  " + debug + "> this = " + Format.objectToStringLong (objRef));
			}
		}

	}

	public void printCallTree () { values.printCallTree(); }
	private void drawGraph (String loc, ThreadReference thr, Method meth) {
		List<StackFrame> frames;
		try {
			frames = thr.frames ();
		} catch (IncompatibleThreadStateException e) {
			throw new Error (Trace.BAD_ERROR_MESSAGE);
		}
		//setDrawPrefixFromParameter (Format.getFrame (meth, thr), meth);
		StackFrame currFrame = Format.getFrame (meth, thr);
		List<LocalVariable> locals;
		try {
			locals = currFrame.visibleVariables ();
		} catch (AbsentInformationException e) {
			return;
		}
		String name = meth.name ();
		if (Trace.CALLBACK_DRAW_THIS_FRAME.equals (name)) {
			List<StackFrame> thisFrame = new ArrayList<> ();
			try {
				thisFrame.add (thr.frame (1));
			} catch (IncompatibleThreadStateException e) {
				throw new Error (Trace.BAD_ERROR_MESSAGE);
			}
			Graphviz.drawFrames (0, loc, null, null, thisFrame, null);
		} else if (Trace.CALLBACK_DRAW_ALL_FRAMES.equals (name) || locals.size () == 0) {
			Graphviz.drawFrames (1, loc, null, null, frames, staticClasses);
		} else if (Trace.CALLBACK_DRAW_OBJECT.equals (name)) {
			ObjectReference obj = (ObjectReference) currFrame.getValue (locals.get (0));
			Map<String, ObjectReference> objects = new HashMap<> ();
			objects.put (Graphviz.PREFIX_UNUSED_LABEL, obj);
			Graphviz.drawObjects (loc, objects);
		} else if (Trace.CALLBACK_DRAW_OBJECT_NAMED.equals (name)) {
			StringReference str = (StringReference) currFrame.getValue (locals.get (0));
			ObjectReference obj = (ObjectReference) currFrame.getValue (locals.get (1));
			Map<String, ObjectReference> objects = new HashMap<> ();
			objects.put (str.value (), obj);
			Graphviz.drawObjects (loc, objects);
		} else if (Trace.CALLBACK_DRAW_OBJECTS_NAMED.equals (name)) {
			ArrayReference args = (ArrayReference) currFrame.getValue (locals.get (0));
			Map<String, ObjectReference> objects = new HashMap<> ();
			int n = args.length ();
			if (n % 2 != 0) throw new Error ("\n!!!! " + Trace.CALLBACK_DRAW_OBJECTS_NAMED + " requires an even number of parameters, alternating strings and objects.");
			for (int i = 0; i < n; i += 2) {
				Value str = args.getValue (i);
				if (!(str instanceof StringReference)) throw new Error ("\n!!!! " + Trace.CALLBACK_DRAW_OBJECTS_NAMED
						+ " requires an even number of parameters, alternating strings and objects.");
				objects.put (((StringReference) str).value (), (ObjectReference) args.getValue (i + 1));
			}
			Graphviz.drawObjects (loc, objects);
		} else {
			ArrayReference args = (ArrayReference) currFrame.getValue (locals.get (0));
			Map<String, ObjectReference> objects = new HashMap<> ();
			int n = args.length ();
			for (int i = 0; i < n; i++) {
				objects.put (Graphviz.PREFIX_UNUSED_LABEL + i, (ObjectReference) args.getValue (i));
			}
			Graphviz.drawObjects (loc, objects);
		}
	}
	// This method was used to set the filename from the parameter of the draw method.
	// Not using this any more.
//	private void setDrawPrefixFromParameter (StackFrame currFrame, Method meth) {
//		String prefix = null;
//		List<LocalVariable> locals;
//		try {
//			locals = currFrame.visibleVariables ();
//		} catch (AbsentInformationException e) {
//			return;
//		}
//		if (locals.size () >= 1) {
//			Value v = currFrame.getValue (locals.get (0));
//			if (!(v instanceof StringReference)) throw new Error ("\n!!!! " + meth.name () + " must have at most a single parameter."
//					+ "\n!!!! The parameter must be of type String");
//			prefix = ((StringReference) v).value ();
//			if (prefix != null) {
//				Graphviz.setOutputFilenamePrefix (prefix);
//			}
//		}
//	}

	// ---------------------- print locals ----------------------------------

	private void printLocals (StackFrame currFrame, ThreadReference thr) {
		List<LocalVariable> locals;
		try {
			locals = currFrame.visibleVariables ();
		} catch (AbsentInformationException e) {
			return;
		}
		String debug = Trace.DEBUG ? "#3" : "";

		ObjectReference objRef = currFrame.thisObject (); // get 'this' object
		if (objRef != null) {
			if (Format.tooManyFields (objRef)) {
				println (thr, "  " + debug + "this: " + Format.objectToStringShort (objRef));
				ReferenceType type = objRef.referenceType (); // get type (class) of object
				List<Field> fields; // use allFields() to include inherited fields
				try {
					fields = type.fields ();
				} catch (ClassNotPreparedException e) {
					throw new Error (Trace.BAD_ERROR_MESSAGE);
				}

				//println (thr, "  fields: ");
				for (Field f : fields) {
					if (!Format.isObjectField (f)) continue;
					println (thr, "  " + debug + "| " + Format.objectToStringShort (objRef) + "." + f.name () + " = " + Format.valueToString (objRef.getValue (f)));
				}
				if (locals.size () > 0) println (thr, "  locals: ");
			} else {
				println (thr, "  " + debug + "| this = " + Format.objectToStringLong (objRef));
			}
		}
		for (LocalVariable l : locals)
			println (thr, "  " + debug + "| " + l.name () + " = " + Format.valueToString (currFrame.getValue (l)));
	}

	// ---------------------- indented printing ----------------------------------

	private boolean atNewLine = true;
	private static PrintStream out = System.out;
	public static void setFilename (String s) {
		try {
			Printer.out = new PrintStream (s);
		} catch (FileNotFoundException e) {
			System.err.println ("Attempting setFilename \"" + s + "\"");
			System.err.println ("Cannot open file \"" + s + "\" for writing; using the console for output.");
		}
	}
	public static void setFilename () {
		Printer.out = System.out;
	}
	public void println (String string) {
		if (!atNewLine) {
			atNewLine = true;
			Printer.out.println ();
		}
		Printer.out.println (string);
	}
	public void println (ThreadReference thr, String string) {
		if (!atNewLine) {
			atNewLine = true;
			Printer.out.println ();
		}
		if (values.numThreads () > 1) Printer.out.format ("%-9s: ", thr.name ());
		int numFrames = (Trace.CONSOLE_SHOW_CALLS || Trace.CONSOLE_SHOW_STEPS) ? values.numFrames (thr) : 0;
		for (int i = 1; i < numFrames; i++)
			Printer.out.print ("  ");
		Printer.out.println (string);
	}
	private void printLinePrefix (ThreadReference thr, boolean showLinePrompt) {
		if (atNewLine) {
			atNewLine = false;
			if (values.numThreads () > 1) Printer.out.format ("%-9s: ", thr.name ());
			int numFrames = (Trace.CONSOLE_SHOW_CALLS || Trace.CONSOLE_SHOW_STEPS) ? values.numFrames (thr) : 0;
			for (int i = 1; i < numFrames; i++)
				Printer.out.print ("  ");
			if (showLinePrompt) Printer.out.print ("  Line: ");
		}
	}
	public void printLineNum (ThreadReference thr, int lineNumber) {
		printLinePrefix (thr, true);
		Printer.out.print (lineNumber + " ");
	}
	public void printDrawEvent (ThreadReference thr, String filename) {
		printLinePrefix (thr, false);
		Printer.out.print ("#" + filename + "# ");
	}
}

/**
 * Code for formatting values and other static utilities.
 *
 * @author James Riely, jriely@cs.depaul.edu, August 2014
 */
/* private static */class Format {
	private Format () {}; // noninstantiable class

	public static StackFrame getFrame (Method meth, ThreadReference thr) {
		Type methDeclaredType = meth.declaringType ();
		int frameNumber = -1;
		StackFrame currFrame;
		try {
			do {
				frameNumber++;
				currFrame = thr.frame (frameNumber);
			} while (methDeclaredType != currFrame.location ().declaringType ());
		} catch (IncompatibleThreadStateException e) {
			throw new Error (Trace.BAD_ERROR_MESSAGE);
		}
		return currFrame;
	}
	// http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
	public static String glob2regex (String glob) {
		StringBuilder regex = new StringBuilder ("^");
		for(int i = 0; i < glob.length(); ++i) {
			final char c = glob.charAt(i);
			switch(c) {
			case '*': regex.append (".*"); break;
			case '.': regex.append ("\\."); break;
			case '$': regex.append ("\\$"); break;
			default: regex.append (c);
			}
		}
		regex.append ('$');
		return regex.toString ();
	}
	private static final ArrayList<String> EXCLUDE_REGEX;
	private static final ArrayList<String> DRAWING_INCLUDE_REGEX;
	static {
		EXCLUDE_REGEX = new ArrayList<> ();
		for (String s : Trace.EXCLUDE_GLOBS) {
			//System.err.println (glob2regex (s));
		    EXCLUDE_REGEX.add (glob2regex (s));
		}
		DRAWING_INCLUDE_REGEX = new ArrayList<> ();
		for (String s : Trace.DRAWING_INCLUDE_GLOBS) {
			DRAWING_INCLUDE_REGEX.add (glob2regex (s));
		}
	}
	public static boolean matchesExcludePrefix (String typeName) {
		//System.err.println (typeName + ":" + Trace.class.getName());
		if (!Trace.SHOW_STRINGS_AS_PRIMITIVE && "java.lang.String".equals (typeName)) return false;
		// don't explore objects on the exclude list
		for (String regex : Format.EXCLUDE_REGEX)
			if (typeName.matches (regex)) return true;
		return false;
	}
	public static boolean matchesExcludePrefixShow (String typeName) {
		for (String regex : Format.DRAWING_INCLUDE_REGEX)
			if (typeName.matches (regex)) return false;
		return matchesExcludePrefix (typeName);
	}
	public static String valueToString (Value value) {
		return valueToString (false, new HashSet<> (), value);
	}
	private static String valueToString (boolean inArray, Set<Value> visited, Value value) {
		if (value == null) return "null";
		if (value instanceof PrimitiveValue) return value.toString ();
		if (Trace.SHOW_STRINGS_AS_PRIMITIVE && value instanceof StringReference) return value.toString ();
		if (Trace.SHOW_BOXED_PRIMITIVES_AS_PRIMITIVE && isWrapper (value.type ())) return wrapperToString ((ObjectReference) value);
		return objectToStringLong (inArray, visited, (ObjectReference) value);
	}
	public static String valueToStringShort (Value value) {
		if (value == null) return "null";
		if (value instanceof PrimitiveValue) return value.toString ();
		if (Trace.SHOW_STRINGS_AS_PRIMITIVE && value instanceof StringReference) return value.toString ();
		if (Trace.SHOW_BOXED_PRIMITIVES_AS_PRIMITIVE && isWrapper (value.type ())) return wrapperToString ((ObjectReference) value);
		return objectToStringShort ((ObjectReference) value);
	}
	public static boolean isWrapper (Type type) {
		if (!(type instanceof ReferenceType)) return false;
		if (type instanceof ArrayType) return false;
		String fqn = type.name ();
		if (!fqn.startsWith ("java.lang.")) return false;
		String className = fqn.substring (10);
		if (className.equals ("String")) return false;
		return (className.equals ("Integer") || className.equals ("Double") || className.equals ("Float") || className.equals ("Long") || className.equals ("Character")
				|| className.equals ("Short") || className.equals ("Byte") || className.equals ("Boolean"));
	}
	public static String wrapperToString (ObjectReference obj) {
		Object xObject;
		if (obj == null) return "null";
		ReferenceType cz = (ReferenceType) obj.type ();
		String fqn = cz.name ();
		String className = fqn.substring (10);
		Field field = cz.fieldByName ("value");
		return obj.getValue (field).toString ();
	}
	public static String objectToStringShort (ObjectReference objRef) {
		if (Trace.CONSOLE_SHOW_TYPE_IN_OBJECT_NAME) return shortenFullyQualifiedName (objRef.type ().name ()) + "@" + objRef.uniqueID ();
		else return "@" + objRef.uniqueID ();
	}
	private static String emptyArrayToStringShort (ArrayReference arrayRef, int length) {
		if (Trace.CONSOLE_SHOW_TYPE_IN_OBJECT_NAME) {
			String classname = shortenFullyQualifiedName (arrayRef.type ().name ());
			return classname.substring (0, classname.indexOf ("[")) + "[" + length + "]@" + arrayRef.uniqueID ();
		} else {
			return "@" + arrayRef.uniqueID ();
		}
	}
	private static String nonemptyArrayToStringShort (ArrayReference arrayRef, int length) {
		if (Trace.CONSOLE_SHOW_TYPE_IN_OBJECT_NAME) return shortenFullyQualifiedName (arrayRef.getValue (0).type ().name ()) + "[" + length + "]@" + arrayRef.uniqueID ();
		else return "@" + arrayRef.uniqueID ();
	}

	public static String objectToStringLong (ObjectReference objRef) {
		return objectToStringLong (false, new HashSet<> (), objRef);
	}
	private static String objectToStringLong (boolean inArray, Set<Value> visited, ObjectReference objRef) {
		if (!visited.add (objRef)) return objectToStringShort (objRef);
		StringBuilder result = new StringBuilder ();
		if (objRef == null) {
			return "null";
		} else if (objRef instanceof ArrayReference) {
			ArrayReference arrayRef = (ArrayReference) objRef;
			int length = arrayRef.length ();
			if (length == 0 || arrayRef.getValue (0) == null) {
				if (!inArray || Trace.CONSOLE_SHOW_NESTED_ARRAY_IDS) {
					result.append (emptyArrayToStringShort (arrayRef, length));
					result.append (" ");
				}
				result.append ("[ ] ");
			} else {
				if (!inArray || Trace.CONSOLE_SHOW_NESTED_ARRAY_IDS) {
					result.append (nonemptyArrayToStringShort (arrayRef, length));
					result.append (" ");
				}
				result.append ("[ ");
				int max = (arrayRef.getValue (0) instanceof PrimitiveValue) ? Trace.CONSOLE_MAX_ARRAY_ELEMENTS_PRIMITIVE : Trace.CONSOLE_MAX_ARRAY_ELEMENTS_OBJECT;
				int i = 0;
				while (i < length && i < max) {
					result.append (valueToString (true, visited, arrayRef.getValue (i)));
					i++;
					if (i < length) result.append (", ");
				}
				if (i < length) result.append ("...");
				result.append (" ]");
			}
		} else {
			result.append (objectToStringShort (objRef));
			ReferenceType type = objRef.referenceType (); // get type (class) of object

			// don't explore objects on the exclude list
			if (!Format.matchesExcludePrefixShow (type.name ())) {
				Iterator<Field> fields; // use allFields() to include inherited fields
				try {
					fields = type.fields ().iterator ();
				} catch (ClassNotPreparedException e) {
					throw new Error (Trace.BAD_ERROR_MESSAGE);
				}
				if (fields.hasNext ()) {
					result.append (" { ");
					int i = 0;
					while (fields.hasNext () && i < Trace.CONSOLE_MAX_FIELDS) {
						Field f = fields.next ();
						if (!isObjectField (f)) continue;
						if (i != 0) result.append (", ");
						result.append (f.name ());
						result.append ("=");
						result.append (valueToString (inArray, visited, objRef.getValue (f)));
						i++;
					}
					if (fields.hasNext ()) result.append ("...");
					result.append (" }");
				}
			}
		}
		return result.toString ();
	}

	// ---------------------- static utilities ----------------------------------

	public static boolean ignoreThread (ThreadReference thr) {
		if (thr.name ().equals ("Signal Dispatcher") || thr.name ().equals ("DestroyJavaVM") || thr.name ().startsWith ("AWT-")) return true; // ignore AWT threads
		if (thr.threadGroup ().name ().equals ("system")) return true; // ignore system threads
		return false;
	}

	public static boolean isStaticField (Field f) {
		if (!Trace.SHOW_SYNTHETIC_FIELDS && f.isSynthetic ()) return false;
		return f.isStatic ();
	}
	public static boolean isObjectField (Field f) {
		if (!Trace.SHOW_SYNTHETIC_FIELDS && f.isSynthetic ()) return false;
		return !f.isStatic ();
	}
	public static boolean isConstructor (Method m) {
		if (!Trace.SHOW_SYNTHETIC_METHODS && m.isSynthetic ()) return false;
		return m.isConstructor ();
	}
	public static boolean isObjectMethod (Method m) {
		if (!Trace.SHOW_SYNTHETIC_METHODS && m.isSynthetic ()) return false;
		return !m.isConstructor () && !m.isStatic ();
	}
	public static boolean isClassMethod (Method m) {
		if (!Trace.SHOW_SYNTHETIC_METHODS && m.isSynthetic ()) return false;
		return m.isStatic ();
	}
	public static boolean tooManyFields (ObjectReference objRef) {
		int count = 0;
		ReferenceType type = ((ReferenceType) objRef.type ());
		for (Field field : type.fields ())
			if (isObjectField (field)) count++;
		return count > Trace.CONSOLE_MAX_FIELDS;
	}
	public static String shortenFullyQualifiedName (String fqn) {
		if (Trace.SHOW_PACKAGE_IN_CLASS_NAME || !fqn.contains (".")) return fqn;
		String className = fqn.substring (1 + fqn.lastIndexOf ("."));
		if (Trace.SHOW_OUTER_CLASS_IN_CLASS_NAME || !className.contains ("$")) return className;
		return className.substring (1 + className.lastIndexOf ("$"));
	}
	public static String shortenFilename (String fn) {
		if (!fn.contains ("/")) return fn;
		return fn.substring (1 + fn.lastIndexOf ("/"));
	}
	public static String fieldToString (Field f) {
		StringBuilder result = new StringBuilder ();
		if (f.isPrivate ()) result.append ("- ");
		if (f.isPublic ()) result.append ("+ ");
		if (f.isPackagePrivate ()) result.append ("~ ");
		if (f.isProtected ()) result.append ("# ");
		result.append (shortenFullyQualifiedName (f.name ()));
		result.append (" : ");
		result.append (shortenFullyQualifiedName (f.typeName ()));
		return result.toString ();
	}
	public static String methodToString (Method m, boolean showClass) {
		return methodToString (m, showClass, true, ".");
	}
	public static String methodToString (Method m, boolean showClass, boolean showParameters, String dotCharacter) {
		String className = shortenFullyQualifiedName (m.declaringType ().name ());
		StringBuilder result = new StringBuilder ();
		if (!showClass && showParameters) {
			if (m.isPrivate ()) result.append ("- ");
			if (m.isPublic ()) result.append ("+ ");
			if (m.isPackagePrivate ()) result.append ("~ ");
			if (m.isProtected ()) result.append ("# ");
		}
		if (m.isConstructor ()) {
			result.append (className);
		} else if (m.isStaticInitializer ()) {
			result.append (className);
			result.append (".CLASS_INITIALIZER");
			return result.toString ();
		} else {
			if (showClass) {
				result.append (className);
				result.append (dotCharacter);
			}
			result.append (shortenFullyQualifiedName (m.name ()));
		}
		if (showParameters) {
			result.append ("(");
			Iterator<LocalVariable> vars;
			try {
				vars = m.arguments ().iterator ();
				while (vars.hasNext ()) {
					result.append (shortenFullyQualifiedName (vars.next ().typeName ()));
					if (vars.hasNext ()) result.append (", ");
				}
			} catch (AbsentInformationException e) {
				result.append ("??");
			}
			result.append (")");
		}
		//result.append (" from ");
		//result.append (m.declaringType ());
		return result.toString ();
	}
}

/**
 * A map from filenames to file contents. Allows lines to be printed.
 *
 * changes: Riely inlined the "ShowLines" class.
 *
 * @author Andrew Davison, March 2009, ad@fivedots.coe.psu.ac.th
 * @author James Riely
 **/
/* private static */class CodeMap {
	private TreeMap<String, ArrayList<String>> listings = new TreeMap<> ();

	// add filename-ShowLines pair to map
	public void addFile (String filename) {
		if (listings.containsKey (filename)) {
			//System.err.println (filename + "already listed");
			return;
		}

		ArrayList<String> code = new ArrayList<> ();
		BufferedReader in = null;
		try {
			in = new BufferedReader (new FileReader ("src/" + filename));
			String line;
			while ((line = in.readLine ()) != null)
				code.add (line);
		} catch (IOException ex) {
			System.err.println ("\n!!!! Problem reading " + filename);
		} finally {
			try {
				if (in != null) in.close ();
			} catch (IOException e) {
				throw new Error ("\n!!!! Problem with " + filename);
			}
		}
		listings.put (filename, code);
		//println (filename + " added to listings");
	}

	// return the specified line from filename
	public String show (String filename, int lineNumber) {
		ArrayList<String> code = listings.get (filename);
		if (code == null) return (filename + " not listed");
		if ((lineNumber < 1) || (lineNumber > code.size ())) return "Line no. out of range";
		return (code.get (lineNumber - 1));
	}

}

/**
 * Map from threads to booleans.
 *
 * @author James Riely, jriely@cs.depaul.edu, August 2014
 */
/* private static */class InsideIgnoredMethodMap {
	// Stack is probably unnecessary here.  A single boolean would do.
	private HashMap<ThreadReference, Stack<Boolean>> map = new HashMap<> ();
	public void removeThread (ThreadReference thr) {
		map.remove (thr);
	}
	public void addThread (ThreadReference thr) {
		Stack<Boolean> st = new Stack<> ();
		st.push (false);
		map.put (thr, st);
	}
	public void enteringIgnoredMethod (ThreadReference thr) {
		map.get (thr).push (true);
	}
	public boolean leavingIgnoredMethod (ThreadReference thr) {
		Stack<Boolean> insideStack = map.get (thr);
		boolean result = insideStack.peek ();
		if (result) insideStack.pop ();
		return result;
	}
	public boolean insideIgnoredMethod (ThreadReference thr) {
		return map.get (thr).peek ();
	}
}

/** From sedgewick and wayne */
/* private static */class Stack<T> {
	private int N;
	private Node<T> first;
	private static class Node<T> {
		T item;
		Node<T> next;
	}
	public Stack () {
		first = null;
		N = 0;
	}
	public boolean isEmpty () {
		return first == null;
	}
	public int size () {
		return N;
	}
	public void push (T item) {
		Node<T> oldfirst = first;
		first = new Node<> ();
		first.item = item;
		first.next = oldfirst;
		N++;
	}
	public T pop () {
		if (isEmpty ()) throw new NoSuchElementException ("Stack underflow");
		T item = first.item;
		first = first.next;
		N--;
		return item;
	}
	public void pop (int n) {
		for (int i=n; i>0; i--)
			pop ();
	}
	public T peek () {
		if (isEmpty ()) throw new NoSuchElementException ("Stack underflow");
		return first.item;
	}
}

/**
 * Keeps track of values in order to spot changes. This keeps copies of stack
 * variables (frames) and arrays. Does not store objects, since direct changes
 * to fields can be trapped by the JDI.
 *
 * @author James Riely, jriely@cs.depaul.edu, August 2014
 */
/* private static */class ValueMap {
	private HashMap<ThreadReference, Stack<HashMap<LocalVariable, Value>>> stacks = new HashMap<> ();
	private HashMap<ArrayReference, Object[]> arrays = new HashMap<> ();
	private HashMap<ArrayReference, Object[]> staticArrays = new HashMap<> ();
	private HashMap<ArrayReference, String> staticArrayNames = new HashMap<> ();
	private CallTree callTree = new CallTree ();
	public int numThreads () {
		return stacks.size ();
	}
	public void clearCallTree () {
		callTree = new CallTree ();
	}
	public void printCallTree () {
		callTree.output ();
	}
	private static class CallTree {
		private HashMap<ThreadReference, Stack<String>> frameIdsMap = new HashMap<> ();
		private HashMap<ThreadReference, List<String>> gvStringsMap = new HashMap<> ();
		private int frameNumber = 0;

		public void output () {
			if (!Trace.SHOW_CALL_TREE) return;
			Graphviz.drawStuff ("callTree", (out) -> {
				out.println ("rankdir=LR;");
				for (List<String> gvStrings : gvStringsMap.values ())
					for (String s : gvStrings) {
						out.println (s);
					}
			});
		}
		public void pop (ThreadReference thr) {
			if (!Trace.SHOW_CALL_TREE) return;
			if (!Trace.drawStepsOfInternal (thr)) return;

			Stack<String> frameIds = frameIdsMap.get(thr);
			if (!frameIds.isEmpty ())
				frameIds.pop ();
		}
		public void push (StackFrame currFrame, ThreadReference thr) {
			if (!Trace.SHOW_CALL_TREE) return;
			if (!Trace.drawStepsOfInternal (thr)) return;

			Stack<String> frameIds = frameIdsMap.get(thr);
			if (frameIds==null) { frameIds = new Stack<> (); frameIdsMap.put (thr, frameIds); }
			List<String> gvStrings = gvStringsMap.get (thr);
			if (gvStrings==null) { gvStrings = new LinkedList<> (); gvStringsMap.put (thr, gvStrings); }

			String currentFrameId = "f" + frameNumber++;
			StringBuilder sb = new StringBuilder ();
			Method method = currFrame.location ().method ();
			sb.append (currentFrameId);
			sb.append ("[label=\"");
			if (method.isSynthetic ()) sb.append ("!");
			if (!method.isStatic ()) {
				sb.append (Graphviz.quote (Format.valueToStringShort (currFrame.thisObject ())));
				sb.append (":");
			}
			sb.append (Graphviz.quote (Format.methodToString (method, true, false, ".")));
			//sb.append (Graphviz.quote (method.name ()));
			sb.append ("(");
			List<LocalVariable> locals = null;
			try { locals = currFrame.visibleVariables (); } catch (AbsentInformationException e) { }
			if (locals != null) {
				boolean first = true;
				for (LocalVariable l : locals)
					if (l.isArgument ()) {
						if (!first) sb.append (", ");
						else first = false;
						// TODO working here (but I don't remember what I was doing...)
						String valString = Format.valueToString (currFrame.getValue (l));
						//Value val = currFrame.getValue (l);
						//String valString = val==null ? "null" : val.toString ();
						sb.append (Graphviz.quote (valString));
					}
			}
			sb.append (")\"");
			sb.append (Trace.GRAPHVIZ_CALL_TREE_BOX_ATTRIBUTES);
			sb.append ("];");
			gvStrings.add (sb.toString ());
			if (!frameIds.isEmpty ()) {
				gvStrings.add (frameIds.peek () + " -> " + currentFrameId + "[label=\"\"" + Trace.GRAPHVIZ_CALL_TREE_ARROW_ATTRIBUTES + "];");
			}
			frameIds.push (currentFrameId);
		}
	}

	public boolean maybeAdjustAfterException (ThreadReference thr) {
		Stack<HashMap<LocalVariable, Value>> stack = stacks.get (thr);

		// count the number of frames left
		int oldCount = stack.size ();
		int currentCount = 0;
		List<StackFrame> frames;
		try {
			frames = thr.frames ();
		} catch (IncompatibleThreadStateException e) {
			throw new Error (Trace.BAD_ERROR_MESSAGE);
		}

		for (StackFrame frame : frames) {
			String calledMethodClassname = frame.location ().declaringType ().name ();
			if (!Format.matchesExcludePrefix (calledMethodClassname)) currentCount++;
		}

		if (oldCount > currentCount) {
			for (int i = oldCount - currentCount; i > 0; i--) {
				stack.pop ();
				callTree.pop (thr);
			}
			return true;
		}
		return false;
	}
	public int numFrames (ThreadReference thr) {
		return stacks.get (thr).size ();
	}
	public void stackCreate (ThreadReference thr) {
		stacks.put (thr, new Stack<> ());
	}
	public void stackDestroy (ThreadReference thr) {
		stacks.remove (thr);
	}
	public void stackPushFrame (StackFrame currFrame, ThreadReference thr) {
		if (!Trace.CONSOLE_SHOW_VARIABLES) return;
		callTree.push (currFrame, thr);
		List<LocalVariable> locals;
		try {
			locals = currFrame.visibleVariables ();
		} catch (AbsentInformationException e) {
			return;
		}

		Stack<HashMap<LocalVariable, Value>> stack = stacks.get (thr);
		HashMap<LocalVariable, Value> frame = new HashMap<> ();
		stack.push (frame);

		for (LocalVariable l : locals) {
			Value v = currFrame.getValue (l);
			frame.put (l, v);
			if (v instanceof ArrayReference) registerArray ((ArrayReference) v);
		}
	}

	public void stackPopFrame (ThreadReference thr) {
		if (!Trace.CONSOLE_SHOW_VARIABLES) return;
		callTree.pop (thr);
		Stack<HashMap<LocalVariable, Value>> stack = stacks.get (thr);
		stack.pop ();
		// space leak in arrays HashMap: arrays never removed
	}

	public void stackUpdateFrame (Method meth, ThreadReference thr, IndentPrinter printer) {
		if (!Trace.CONSOLE_SHOW_VARIABLES) return;
		StackFrame currFrame = Format.getFrame (meth, thr);
		List<LocalVariable> locals;
		try {
			locals = currFrame.visibleVariables ();
		} catch (AbsentInformationException e) {
			return;
		}
		Stack<HashMap<LocalVariable, Value>> stack = stacks.get (thr);
		if (stack.isEmpty ()) {
			throw new Error ("\n!!!! Frame empty: " + meth + " : " + thr);
		}
		HashMap<LocalVariable, Value> frame = stack.peek ();

		String debug = Trace.DEBUG ? "#1" : "";
		for (LocalVariable l : locals) {
			Value oldValue = frame.get (l);
			Value newValue = currFrame.getValue (l);
			if (valueHasChanged (oldValue, newValue)) {
				frame.put (l, newValue);
				if (newValue instanceof ArrayReference) registerArray ((ArrayReference) newValue);
				String change = (oldValue == null) ? "|" : ">";
				printer.println (thr, "  " + debug + change + " " + l.name () + " = " + Format.valueToString (newValue));
			}
		}

		ObjectReference thisObj = currFrame.thisObject ();
		if (thisObj != null) {
			boolean show = Format.tooManyFields (thisObj);
			if (arrayFieldHasChanged (show, thr, thisObj, printer) && !show) printer.println (thr, "  " + debug + "> this = " + Format.objectToStringLong (thisObj));
		}
		arrayStaticFieldHasChanged (true, thr, printer);
	}

	public void registerArray (ArrayReference val) {
		if (!arrays.containsKey (val)) {
			arrays.put (val, copyArray (val));
		}
	}
	public boolean registerStaticArray (ArrayReference val, String name) {
		if (!staticArrays.containsKey (val)) {
			staticArrays.put (val, copyArray (val));
			staticArrayNames.put (val, name);
			return true;
		}
		return false;
	}
	private static Object[] copyArray (ArrayReference oldArrayReference) {
		Object[] newArray = new Object[oldArrayReference.length ()];
		for (int i = 0; i < newArray.length; i++) {
			Value val = oldArrayReference.getValue (i);
			if (val instanceof ArrayReference) newArray[i] = copyArray ((ArrayReference) val);
			else newArray[i] = val;
		}
		return newArray;
	}

	private boolean valueHasChanged (Value oldValue, Value newValue) {
		if (oldValue == null && newValue == null) return false;
		if (oldValue == null && newValue != null) return true;
		if (oldValue != null && newValue == null) return true;
		if (!oldValue.equals (newValue)) return true;
		if (!(oldValue instanceof ArrayReference)) return false;
		return arrayValueHasChanged ((ArrayReference) oldValue, (ArrayReference) newValue);
	}
	private boolean arrayStaticFieldHasChanged (Boolean show, ThreadReference thr, IndentPrinter printer) {
		boolean result = false;
		boolean print = false;
		String debug = Trace.DEBUG ? "#7" : "";
		String change = ">";
		for (ArrayReference a : staticArrays.keySet ()) {
			Object[] objArray = staticArrays.get (a);
			if (arrayValueHasChangedHelper (objArray, a)) {
				result = true;
				print = true;
			}
			if (show && print) {
				printer.println (thr, "  " + debug + change + " " + staticArrayNames.get (a) + " = " + Format.valueToString (a));
			}
		}
		return result;
	}
	private boolean arrayFieldHasChanged (Boolean show, ThreadReference thr, ObjectReference objRef, IndentPrinter printer) {
		ReferenceType type = objRef.referenceType (); // get type (class) of object
		List<Field> fields; // use allFields() to include inherited fields
		try {
			fields = type.fields ();
		} catch (ClassNotPreparedException e) {
			throw new Error (Trace.BAD_ERROR_MESSAGE);
		}
		boolean result = false;
		String debug = Trace.DEBUG ? "#2" : "";
		String change = ">";
		for (Field f : fields) {
			Boolean print = false;
			Value v = objRef.getValue (f);
			if (!(v instanceof ArrayReference)) continue;
			ArrayReference a = (ArrayReference) v;
			if (!arrays.containsKey (a)) {
				registerArray (a);
				change = "|";
				result = true;
				print = true;
			} else {
				Object[] objArray = arrays.get (a);
				if (arrayValueHasChangedHelper (objArray, a)) {
					result = true;
					print = true;
				}
			}
			if (show && print) {
				printer.println (thr, "  " + debug + change + " " + Format.objectToStringShort (objRef) + "." + f.name () + " = " + Format.valueToString (objRef.getValue (f)));
			}
		}
		return result;
	}
	private boolean arrayValueHasChanged (ArrayReference oldArray, ArrayReference newArray) {
		if (oldArray.length () != newArray.length ()) return true;
		int len = oldArray.length ();
		if (!arrays.containsKey (newArray)) {
			return true;
		}
		Object[] oldObjArray = arrays.get (newArray);
		//            if (oldObjArray.length != len)
		//                throw new Error (Trace.BAD_ERROR_MESSAGE);
		return arrayValueHasChangedHelper (oldObjArray, newArray);
	}
	private boolean arrayValueHasChangedHelper (Object[] oldObjArray, ArrayReference newArray) {
		int len = oldObjArray.length;
		boolean hasChanged = false;
		for (int i = 0; i < len; i++) {
			Object oldObject = oldObjArray[i];
			Value newVal = newArray.getValue (i);
			if (oldObject == null && newVal != null) {
				oldObjArray[i] = newVal;
				hasChanged = true;
			}
			if (oldObject instanceof Value && valueHasChanged ((Value) oldObject, newVal)) {
				//System.out.println ("BOB:" + i + ":" + oldObject + ":" + newVal);
				oldObjArray[i] = newVal;
				hasChanged = true;
			}
			if (oldObject instanceof Object[]) {
				//if (!(newVal instanceof ArrayReference)) throw new Error (Trace.BAD_ERROR_MESSAGE);
				if (arrayValueHasChangedHelper ((Object[]) oldObject, (ArrayReference) newVal)) {
					hasChanged = true;
				}
			}
		}
		return hasChanged;
	}
}

/* private static */class Graphviz {
	private Graphviz () {} // noninstantiable class
	//- This code is based on LJV:
	// LJV.java --- Generate a graph of an object, using Graphviz
	// The Lightweight Java Visualizer (LJV)
	// https://www.cs.auckland.ac.nz/~j-hamer/

	//- Author:     John Hamer <J.Hamer@cs.auckland.ac.nz>
	//- Created:    Sat May 10 15:27:48 2003
	//- Time-stamp: <2004-08-23 12:47:06 jham005>

	//- Copyright (C) 2004  John Hamer, University of Auckland
	//-
	//-   This program is free software; you can redistribute it and/or
	//-   modify it under the terms of the GNU General Public License
	//-   as published by the Free Software Foundation; either version 2
	//-   of the License, or (at your option) any later version.
	//-
	//-   This program is distributed in the hope that it will be useful,
	//-   but WITHOUT ANY WARRANTY; without even the implied warranty of
	//-   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	//-   GNU General Public License for more details.
	//-
	//-   You should have received a copy of the GNU General Public License along
	//-   with this program; if not, write to the Free Software Foundation, Inc.,
	//-   59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.

	//- $Id: LJV.java,v 1.1 2004/07/14 02:03:45 jham005 Exp $
	//

	/**
	 * Graphics files are saved in directory dirName/mainClassName.
	 * dirName directory is created if it does not already exist.
	 * If dirName/mainClassName exists, then numbers are appended to the directory name:
	 * "dirName/mainClassName 1", "dirName/mainClassName 2", etc.
	 */
	public static void setOutputDirectory (String dirName, String mainClassName) {
		if (dirName == null || mainClassName == null) {
			throw new Error ("\n!!!! no nulls please");
		}
		Graphviz.dirName = dirName;
		Graphviz.mainClassName = mainClassName;
	}
	private static String dirName;
	private static String mainClassName;
	
	/**
	 * The name of the output file is derived from <code>baseFilename</code> by
	 * appending successive integers.
	 */
	public static String peekFilename () {
		return String.format ("%03d", nextGraphNumber);
	}
	private static String nextFilename () {
		if (baseFilename == null) setBaseFilename ();		
		++nextGraphNumber;
		return baseFilename + peekFilename ();		
	}
	private static int nextGraphNumber = -1;
	private static String baseFilename = null;
	private static void setBaseFilename () {
		if (dirName == null || mainClassName == null) {
			throw new Error ("\n!!!! no call to setOutputDirectory");
		}
		// create dir
		File dir = new File (dirName);
		if (!dir.isAbsolute ()) {
			dirName = System.getProperty ("user.home") + File.separator + dirName;
			dir = new File (dirName);
		}
		if (dir.exists ()) {
			if (!dir.isDirectory ()) 
				throw new Error ("\n!!!! \"" + dir + "\" is not a directory");
			if (!dir.canWrite ()) 
				throw new Error ("\n!!!! Unable to write directory: \"" + dir + "\"");
		} else {
			dir.mkdirs ();
		}
		
		// create newDir
		String prefix = dirName + File.separator;
		String[] mainClassPath = mainClassName.split ("\\.");
		mainClassName = mainClassPath[mainClassPath.length-1];
		File newDir = new File (prefix + mainClassName);
		int suffix = 0;
		while (newDir.exists()) { 
			suffix++; 
			newDir = new File(prefix + mainClassName + " " + suffix);
		}
		newDir.mkdir ();
		
		if (!newDir.isDirectory () || !newDir.canWrite ())
			throw new Error ("Failed setOutputDirectory \"" + newDir + "\"");
		baseFilename = newDir + File.separator;
		nextGraphNumber = -1;
	}
//	/** @deprecated */
//	private static void setOutputFilenamePrefix (String s) {
//		File f = new File (s);
//		String fCanonical;
//		try { 
//			fCanonical = f.getCanonicalPath ();
//		} catch (IOException e) {
//			throw new Error ("Failed setBaseFilename \"" + f + "\"");
//		}
//
//		String newBaseFilename;
//		if (f.isDirectory ()) { 			
//			if (f.canWrite ()) {
//				newBaseFilename = fCanonical + "/trace-"; 
//			} else {
//				throw new Error ("Failed setBaseFilename \"" + f + "\"");
//			}
//		} else {
//			File parent = (f == null) ? null : f.getParentFile ();
//			if (parent == null || parent.canWrite ()) {
//				newBaseFilename = fCanonical;
//			} else {
//				System.err.println ("Cannot open directory \"" + f.getParent () + "\" for writing; using the current directory for graphziv output.");
//				throw new Error ("Failed setBaseFilename \"" + f + "\"");
//			}
//		}
//		if (!newBaseFilename.equals (baseFilename)) {
//			baseFilename = newBaseFilename;
//			nextGraphNumber = -1;
//		}
//	}

	public static final HashMap<String, String> objectAttributeMap = new HashMap<> ();
	public static final HashMap<String, String> staticClassAttributeMap = new HashMap<> ();
	public static final HashMap<String, String> frameAttributeMap = new HashMap<> ();
	public static final HashMap<String, String> fieldAttributeMap = new HashMap<> ();

	// ----------------------------------- utilities -----------------------------------------------

	private static boolean canTreatAsPrimitive (Value v) {
		if (v == null || v instanceof PrimitiveValue) return true;
		if (Trace.SHOW_STRINGS_AS_PRIMITIVE && v instanceof StringReference) return true;
		if (Trace.SHOW_BOXED_PRIMITIVES_AS_PRIMITIVE && Format.isWrapper (v.type ())) return true;
		return false;
	}
	private static boolean looksLikePrimitiveArray (ArrayReference obj) {
		try {
			if (((ArrayType) obj.type ()).componentType () instanceof PrimitiveType) return true;
		} catch (ClassNotLoadedException e) {
			return false;
		}

		for (int i = 0, len = obj.length (); i < len; i++)
			if (!canTreatAsPrimitive (obj.getValue (i))) return false;
		return true;
	}
	private static boolean canIgnoreObjectField (Field field) {
		if (!Format.isObjectField (field)) return true;
		for (String ignoredField : Trace.GRAPHVIZ_IGNORED_FIELDS)
			if (ignoredField.equals (field.name ())) return true;
		return false;
	}
	private static boolean canIgnoreStaticField (Field field) {
		if (!Format.isStaticField (field)) return true;
		for (String ignoredField : Trace.GRAPHVIZ_IGNORED_FIELDS)
			if (ignoredField.equals (field.name ())) return true;
		return false;
	}

	//private static final String canAppearUnquotedInLabelChars = " /$&*@#!-+()^%;_[],;.=";
	private static boolean canAppearUnquotedInLabel (char c) {
		return true;
		//return canAppearUnquotedInLabelChars.indexOf (c) != -1 || Character.isLetter (c) || Character.isDigit (c);
	}
	private static final String quotable = "\\\"<>{}|";
	protected static String quote (String s) {
		s = unescapeJavaString (s);
		StringBuffer sb = new StringBuffer ();
		for (int i = 0, n = s.length (); i < n; i++) {
			char c = s.charAt (i);
			if (quotable.indexOf (c) != -1) sb.append ('\\').append (c);
			else if (canAppearUnquotedInLabel (c)) sb.append (c);
			else sb.append ("\\\\u").append (Integer.toHexString (c));
		}
		return sb.toString ();
	}
	/**
	 * Unescapes a string that contains standard Java escape sequences.
	 * <ul>
	 * <li><strong>\\b \\f \\n \\r \\t \\" \\'</strong> :
	 * BS, FF, NL, CR, TAB, double and single quote.</li>
	 * <li><strong>\\N \\NN \\NNN</strong> : Octal character
	 * specification (0 - 377, 0x00 - 0xFF).</li>
	 * <li><strong>\\uNNNN</strong> : Hexadecimal based Unicode character.</li>
	 * </ul>
	 *
	 * @param st
	 *            A string optionally containing standard java escape sequences.
	 * @return The translated string.
	 */
	// from http://udojava.com/2013/09/28/unescape-a-string-that-contains-standard-java-escape-sequences/
	private static String unescapeJavaString(String st) {
		StringBuilder sb = new StringBuilder(st.length());
		for (int i = 0; i < st.length(); i++) {
			char ch = st.charAt(i);
			if (ch == '\\') {
				char nextChar = (i == st.length() - 1) ? '\\' : st.charAt(i + 1);
				// Octal escape?
				if (nextChar >= '0' && nextChar <= '7') {
					String code = "" + nextChar;
					i++;
					if ((i < st.length() - 1) && st.charAt(i + 1) >= '0' && st.charAt(i + 1) <= '7') {
						code += st.charAt(i + 1);
						i++;
						if ((i < st.length() - 1) && st.charAt(i + 1) >= '0' && st.charAt(i + 1) <= '7') {
							code += st.charAt(i + 1);
							i++;
						}
					}
					sb.append((char) Integer.parseInt(code, 8));
					continue;
				}
				switch (nextChar) {
				case '\\': ch = '\\'; break;
				case 'b': ch = '\b'; break;
				case 'f': ch = '\f'; break;
				case 'n': ch = '\n'; break;
				case 'r': ch = '\r'; break;
				case 't': ch = '\t'; break;
				case '\"': ch = '\"'; break;
				case '\'': ch = '\''; break;
				// Hex Unicode: u????
				case 'u':
					if (i >= st.length() - 5) { ch = 'u'; break; }
					int code = Integer.parseInt(st.substring (i+2,i+6), 16);
					sb.append(Character.toChars(code));
					i += 5;
					continue;
				}
				i++;
			}
			sb.append(ch);
		}
		return sb.toString();
	}



	// ----------------------------------- values -----------------------------------------------

	protected static final String PREFIX_UNUSED_LABEL = "_____";
	private static final String PREFIX_LABEL = "L";
	private static final String PREFIX_ARRAY = "A";
	private static final String PREFIX_OBJECT = "N";
	private static final String PREFIX_STATIC = "S";
	private static final String PREFIX_FRAME = "F";
	private static final String PREFIX_RETURN = "returnValue";
	private static final String PREFIX_EXCEPTION = "exception";

	private static void processPrimitiveArray (ArrayReference obj, PrintWriter out) {
		out.print (objectGvName (obj) + "[label=\"");
		if (Trace.GRAPHVIZ_SHOW_OBJECT_IDS) out.print (objectName (obj));
		for (int i = 0, len = obj.length (); i < len; i++) {
			if (Trace.GRAPHVIZ_SHOW_OBJECT_IDS || i != 0) out.print ("|");
			Value v = obj.getValue (i);
			if (v != null) processValueInline (Trace.GRAPHVIZ_SHOW_NULL_FIELDS, "", v, out);
		}
		out.println ("\"" + Trace.GRAPHVIZ_ARRAY_BOX_ATTRIBUTES + "];");
	}
	private static void processObjectArray (ArrayReference obj, PrintWriter out, Set<ObjectReference> visited) {
		out.print (objectGvName (obj) + "[label=\"");
		if (Trace.GRAPHVIZ_SHOW_OBJECT_IDS) out.print (objectName (obj));
		int len = obj.length ();
		for (int i = 0; i < len; i++) {
			if (Trace.GRAPHVIZ_SHOW_OBJECT_IDS || i != 0) out.print ("|");
			out.print ("<" + PREFIX_ARRAY + i + ">");
			if (Trace.GRAPHVIZ_SHOW_OBJECT_IDS_REDUNDANTLY) {
				ObjectReference ref = (ObjectReference) obj.getValue (i);
				out.print (objectNameOnly (ref));
			}
		}
		out.println ("\"" + Trace.GRAPHVIZ_ARRAY_BOX_ATTRIBUTES + "];");
		for (int i = 0; i < len; i++) {
			ObjectReference ref = (ObjectReference) obj.getValue (i);
			if (ref == null) continue;
			out.println (objectGvName (obj) + ":" + PREFIX_ARRAY + i + ":c -> " + objectGvName (ref) + "[label=\"" + i + "\"" + Trace.GRAPHVIZ_ARRAY_ARROW_ATTRIBUTES + "];");
			processObject (ref, out, visited);
		}
	}
	private static void processValueStandalone (String gvSource, String arrowAttributes, String fieldName, Value val, PrintWriter out, Set<ObjectReference> visited) {
		if (canTreatAsPrimitive (val)) throw new Error (Trace.BAD_ERROR_MESSAGE);
		ObjectReference objRef = (ObjectReference) val;
		String GvName = objectGvName (objRef);
		out.println (gvSource + " -> " + GvName + "[label=\"" + fieldName + "\"" + arrowAttributes + "];");
		processObject (objRef, out, visited);
	}
	private static boolean processValueInline (boolean showNull, String prefix, Value val, PrintWriter out) {
		if ((!Trace.GRAPHVIZ_SHOW_OBJECT_IDS_REDUNDANTLY) && (!canTreatAsPrimitive (val))) return false;
		if (val == null && !showNull)
			return false;
		out.print (prefix);
		if (val == null) {
			out.print (quote ("null"));
		} else if (val instanceof PrimitiveValue) {
			out.print (quote (val.toString ()));
		} else if (Trace.SHOW_STRINGS_AS_PRIMITIVE && val instanceof StringReference) {
			out.print (quote (val.toString ()));
		} else if (Trace.SHOW_BOXED_PRIMITIVES_AS_PRIMITIVE && Format.isWrapper (val.type ())) {
			out.print (quote (Format.wrapperToString ((ObjectReference) val)));
		} else if (Trace.GRAPHVIZ_SHOW_OBJECT_IDS_REDUNDANTLY && val instanceof ObjectReference) {
			out.print (quote (objectNameOnly (val)));
		}
		return true;
	}
	// val must be primitive, wrapper or string
	private static void processWrapperAsSimple (String gvName, Value val, PrintWriter out, Set<ObjectReference> visited) {
		String cabs = null;
		out.print (gvName + "[label=\"");
		if (Trace.GRAPHVIZ_SHOW_OBJECT_IDS && val instanceof ObjectReference) {
			out.print (objectNameOnly (val) + " : ");
		}
		if (val instanceof PrimitiveValue) {
			out.print (quote (val.toString ()));
		} else if (val instanceof StringReference) {
			out.print (quote (val.toString ()));
		} else {
			out.print (quote (Format.wrapperToString ((ObjectReference) val)));
		}
		out.println ("\"" + Trace.GRAPHVIZ_WRAPPER_BOX_ATTRIBUTES + (cabs == null ? "" : "," + cabs) + "];");
	}

	// ----------------------------------- objects -----------------------------------------------

	private static String objectNameOnly (Value val) {
		if (val == null) return "null";
		if (!(val instanceof ObjectReference)) return ""; 
		return "@" + ((ObjectReference)val).uniqueID ();
	}
	private static String objectName (ObjectReference obj) {
		if (obj == null) return "";
		String objString = (Trace.GRAPHVIZ_SHOW_OBJECT_IDS) ? "@" + obj.uniqueID () + " : " : "";
		return objString + Format.shortenFullyQualifiedName (obj.type ().name ());
	}
	private static String objectGvName (ObjectReference obj) {
		return PREFIX_OBJECT + obj.uniqueID ();
	}
	private static boolean objectHasPrimitives (List<Field> fs, ObjectReference obj) {
		for (Field f : fs) {
			if (canIgnoreObjectField (f)) continue;
			if (canTreatAsPrimitive (obj.getValue (f))) return true;
			if (Trace.GRAPHVIZ_SHOW_OBJECT_IDS_REDUNDANTLY) return true;
		}
		return false;
	}
	private static void labelObjectWithNoPrimitiveFields (ObjectReference obj, PrintWriter out) {
		String cabs = objectAttributeMap.get (obj.type ().name ());
		out.println (objectGvName (obj) + "[label=\"" + objectName (obj) + "\"" + Trace.GRAPHVIZ_OBJECT_BOX_ATTRIBUTES + (cabs == null ? "" : "," + cabs) + "];");
	}
	private static void labelObjectWithSomePrimitiveFields (ObjectReference obj, List<Field> fs, PrintWriter out) {
		out.print (objectGvName (obj) + "[label=\"" + objectName (obj) + "|{");
		String sep = "";
		for (Field f : fs) {
			String name = Trace.GRAPHVIZ_SHOW_FIELD_NAMES_IN_LABELS ? f.name () + " = " : "";
			if (!canIgnoreObjectField (f)) {
				if (processValueInline (Trace.GRAPHVIZ_SHOW_NULL_FIELDS, sep + name, obj.getValue (f), out)) sep = "|";
			} 
		}
		String cabs = objectAttributeMap.get (obj.type ().name ());
		out.println ("}\"" + Trace.GRAPHVIZ_OBJECT_BOX_ATTRIBUTES + (cabs == null ? "" : "," + cabs) + "];");
	}
	private static void processObjectWithLabel (String label, ObjectReference obj, PrintWriter out, Set<ObjectReference> visited) {
		processObject (obj, out, visited);
		if (!label.startsWith (PREFIX_UNUSED_LABEL)) {
			String gvObjName = objectGvName (obj);
			String gvLabelName = PREFIX_LABEL + label;
			out.println (gvLabelName + "[label=\"" + label + "\"" + Trace.GRAPHVIZ_LABEL_BOX_ATTRIBUTES + "];");
			out.println (gvLabelName + " -> " + gvObjName + "[label=\"\"" + Trace.GRAPHVIZ_LABEL_ARROW_ATTRIBUTES + "];");
		}
	}
	private static Value valueByFieldname (ObjectReference obj, String fieldName) {
		ReferenceType type = (ReferenceType) obj.type ();
		Field field = type.fieldByName (fieldName);
		return obj.getValue (field);
	}
	private static void processObject (ObjectReference obj, PrintWriter out, Set<ObjectReference> visited) {
		if (visited.add (obj)) {
			Type type = obj.type ();
			String typeName = type.name ();

			if (!Trace.SHOW_BOXED_PRIMITIVES_AS_PRIMITIVE && Trace.GRAPHVIZ_SHOW_BOXED_PRIMITIVES_SIMPLY && Format.isWrapper (type)) {
				processWrapperAsSimple (objectGvName (obj), obj, out, visited);
			} else if (!Trace.SHOW_STRINGS_AS_PRIMITIVE && Trace.GRAPHVIZ_SHOW_BOXED_PRIMITIVES_SIMPLY && obj instanceof StringReference) {
				processWrapperAsSimple (objectGvName (obj), obj, out, visited);
			} else if (obj instanceof ArrayReference) {
				ArrayReference arr = (ArrayReference) obj;
				if (looksLikePrimitiveArray (arr)) processPrimitiveArray (arr, out);
				else processObjectArray (arr, out, visited);
			} else {
				List<Field> fs = ((ReferenceType) type).fields ();
				if (objectHasPrimitives (fs, obj)) labelObjectWithSomePrimitiveFields (obj, fs, out);
				else labelObjectWithNoPrimitiveFields (obj, out);
				if (!Format.matchesExcludePrefixShow (typeName)) {
					//System.err.println (typeName);
					String source = objectGvName (obj);
					for (Field f : fs) {
						Value value = obj.getValue (f);
						if ((!canIgnoreObjectField (f)) && (!canTreatAsPrimitive (value))) {
							processValueStandalone (source, Trace.GRAPHVIZ_OBJECT_ARROW_ATTRIBUTES, f.name (), value, out, visited);
						}
					}
				}
			}
		}
	}

	// ----------------------------------- static classes -----------------------------------------------

	private static String staticClassName (ReferenceType type) {
		return Format.shortenFullyQualifiedName (type.name ());
	}
	private static String staticClassGvName (ReferenceType type) {
		return PREFIX_STATIC + type.classObject ().uniqueID ();
	}
	private static boolean staticClassHasFields (List<Field> fs) {
		for (Field f : fs) {
			if (!canIgnoreStaticField (f)) return true;
		}
		return false;
	}
	private static boolean staticClassHasPrimitives (List<Field> fs, ReferenceType staticClass) {
		for (Field f : fs) {
			if (canIgnoreStaticField (f)) continue;
			if (canTreatAsPrimitive (staticClass.getValue (f))) return true;
			if (Trace.GRAPHVIZ_SHOW_OBJECT_IDS_REDUNDANTLY) return true;
		}
		return false;
	}
	private static void labelStaticClassWithNoPrimitiveFields (ReferenceType type, PrintWriter out) {
		String cabs = staticClassAttributeMap.get (type.name ());
		out.println (staticClassGvName (type) + "[label=\"" + staticClassName (type) + "\"" + Trace.GRAPHVIZ_STATIC_CLASS_BOX_ATTRIBUTES + (cabs == null ? "" : "," + cabs) + "];");
	}
	private static void labelStaticClassWithSomePrimitiveFields (ReferenceType type, List<Field> fs, PrintWriter out) {
		out.print (staticClassGvName (type) + "[label=\"" + staticClassName (type) + "|{");
		String sep = "";
		for (Field field : fs) {
			if (!canIgnoreStaticField (field)) {
				String name = Trace.GRAPHVIZ_SHOW_FIELD_NAMES_IN_LABELS ? field.name () + " = " : "";
				if (processValueInline (Trace.GRAPHVIZ_SHOW_NULL_FIELDS, sep + name, type.getValue (field), out)) sep = "|";
			}
		}
		String cabs = staticClassAttributeMap.get (type.name ());
		out.println ("}\"" + Trace.GRAPHVIZ_STATIC_CLASS_BOX_ATTRIBUTES + (cabs == null ? "" : "," + cabs) + "];");
	}
	private static void processStaticClass (ReferenceType type, PrintWriter out, Set<ObjectReference> visited) {
		String typeName = type.name ();
		List<Field> fs = type.fields ();
		if (!staticClassHasFields (fs)) {
			return;
		}
		if (staticClassHasPrimitives (fs, type)) {
			labelStaticClassWithSomePrimitiveFields (type, fs, out);
		} else {
			labelStaticClassWithNoPrimitiveFields (type, out);
		}
		if (!Format.matchesExcludePrefixShow (type.name ())) {
			String source = staticClassGvName (type);
			for (Field f : fs) {
				if (f.isStatic ()) {
					Value value = type.getValue (f);
					if ((!canIgnoreStaticField (f)) && (!canTreatAsPrimitive (value))) {
						String name = f.name ();
						processValueStandalone (source, Trace.GRAPHVIZ_STATIC_CLASS_ARROW_ATTRIBUTES, name, value, out, visited);
					}
				}
			}
		}
	}

	// ----------------------------------- frames -----------------------------------------------
	private static String frameName (int frameNumber, StackFrame frame, Method method, int lineNumber) {
		String objString = (Trace.GRAPHVIZ_SHOW_FRAME_NUMBERS) ? "@" + frameNumber + " : " : "";
		return objString + Format.methodToString (method, true, false, ".") + " # " + lineNumber;
	}
	private static String frameGvName (int frameNumber) {
		return PREFIX_FRAME + frameNumber;
	}
	private static boolean frameHasPrimitives (Map<LocalVariable, Value> ls) {
		for (LocalVariable lv : ls.keySet ()) {
			Value v = ls.get (lv);
			if (canTreatAsPrimitive (v)) return true;
			if (Trace.GRAPHVIZ_SHOW_OBJECT_IDS_REDUNDANTLY) return true;
		}
		return false;
	}
	private static void labelFrameWithNoPrimitiveLocals (int frameNumber, StackFrame frame, PrintWriter out) {
		Location location = frame.location ();
		ReferenceType type = location.declaringType ();
		Method method = location.method ();
		String attributes = frameAttributeMap.get (type.name ());
		out.println (frameGvName (frameNumber) + "[label=\"" + frameName (frameNumber, frame, method, location.lineNumber ()) + "\"" + Trace.GRAPHVIZ_FRAME_BOX_ATTRIBUTES
				+ (attributes == null ? "" : "," + attributes) + "];");
	}
	private static void labelFrameWithSomePrimitiveLocals (int frameNumber, StackFrame frame, Map<LocalVariable, Value> ls, PrintWriter out) {
		Location location = frame.location ();
		ReferenceType type = location.declaringType ();
		Method method = location.method ();
		out.print (frameGvName (frameNumber) + "[label=\"" + frameName (frameNumber, frame, method, location.lineNumber ()) + "|{");
		String sep = "";
		for (LocalVariable lv : ls.keySet ()) {
			String name = Trace.GRAPHVIZ_SHOW_FIELD_NAMES_IN_LABELS ? lv.name () + " = " : "";
			if (processValueInline (Trace.GRAPHVIZ_SHOW_NULL_VARIABLES, sep + name, ls.get (lv), out)) sep = "|";
		}
		String cabs = frameAttributeMap.get (type.name ());
		out.println ("}\"" + Trace.GRAPHVIZ_FRAME_BOX_ATTRIBUTES + (cabs == null ? "" : "," + cabs) + "];");
	}
	private static boolean processFrame (int frameNumber, StackFrame frame, PrintWriter out, Set<ObjectReference> visited) {
		Location location = frame.location ();
		ReferenceType type = location.declaringType ();
		Method method = location.method ();
		if (Format.matchesExcludePrefixShow (type.name ())) return false;

		Map<LocalVariable, Value> ls;
		try {
			ls = frame.getValues (frame.visibleVariables ());
		} catch (AbsentInformationException e) {
			return false;
		}
		if (frameHasPrimitives (ls)) {
			labelFrameWithSomePrimitiveLocals (frameNumber, frame, ls, out);
		} else {
			labelFrameWithNoPrimitiveLocals (frameNumber, frame, out);
		}
		ObjectReference thisObject = frame.thisObject ();
		if (thisObject != null) processValueStandalone (frameGvName (frameNumber), Trace.GRAPHVIZ_FRAME_OBJECT_ARROW_ATTRIBUTES, "this", thisObject, out, visited);
		for (LocalVariable lv : ls.keySet ()) {
			Value value = ls.get (lv);
			if (!canTreatAsPrimitive (value)) {
				processValueStandalone (frameGvName (frameNumber), Trace.GRAPHVIZ_FRAME_OBJECT_ARROW_ATTRIBUTES, lv.name (), value, out, visited);
			}
		}
		return true;
	}

	// ----------------------------------- top level -----------------------------------------------

	public static void drawFramesCheck (String loc, Value returnVal, Value exnVal, List<StackFrame> frames, Set<ReferenceType> staticClasses) {
		if (Trace.drawStepsOfInternal (frames, returnVal))
			drawFrames (0, loc, returnVal, exnVal, frames, staticClasses);
	}
	public static void drawFrames (int start, String loc, Value returnVal, Value exnVal, List<StackFrame> frames, Set<ReferenceType> staticClasses) {
		drawStuff (loc, (out) -> {
			Set<ObjectReference> visited = new HashSet<> ();
			if (staticClasses != null) {
				for (ReferenceType staticClass : staticClasses) {
					processStaticClass (staticClass, out, visited);
				}
			}
			int len = 0;
			if (frames != null) {
				len = frames.size ();
				for (int i = len - 1, prev = i; i >= start; i--) {
					StackFrame currentFrame = frames.get (i);
					Method meth = currentFrame.location ().method ();
					if (!Trace.SHOW_SYNTHETIC_METHODS && meth.isSynthetic ()) continue;
					if (processFrame (len - i, currentFrame, out, visited)) {
						if (prev != i) {
							out.println (frameGvName (len - i) + " -> " + frameGvName (len - prev) + "[label=\"\"" + Trace.GRAPHVIZ_FRAME_FRAME_ARROW_ATTRIBUTES + "];");
							prev = i;
						}
					}
				}
				// show the return value -- without this, it mysteriously disappears when drawing all steps
				if (returnVal != null && !(returnVal instanceof VoidValue)) {
					String objString = (Trace.GRAPHVIZ_SHOW_FRAME_NUMBERS) ? "@" + (len + 1) + " : " : "";
					if (canTreatAsPrimitive (returnVal)) {
						out.print (PREFIX_RETURN + " [label=\"" + objString + "returnValue = ");
						processValueInline (true, "", returnVal, out);
						out.println ("\"" + Trace.GRAPHVIZ_FRAME_RETURN_ATTRIBUTES + "];");
						out.println (PREFIX_RETURN + " -> " + frameGvName (len) + "[label=\"\"" + Trace.GRAPHVIZ_FRAME_FRAME_ARROW_ATTRIBUTES + "];");
					} else {
						out.println (PREFIX_RETURN + " [label=\"" + objString + "returnValue\"" + Trace.GRAPHVIZ_FRAME_RETURN_ATTRIBUTES + "];");
						processValueStandalone (PREFIX_RETURN, Trace.GRAPHVIZ_FRAME_OBJECT_ARROW_ATTRIBUTES, "", returnVal, out, visited);
						out.println (PREFIX_RETURN + " -> " + frameGvName (len) + "[label=\"\"" + Trace.GRAPHVIZ_FRAME_FRAME_ARROW_ATTRIBUTES + "];");
					}
				}
			}
			// show the exception value
			if (exnVal != null && !(exnVal instanceof VoidValue)) {
				if (canTreatAsPrimitive (exnVal)) {
					out.print (PREFIX_EXCEPTION + " [label=\"exception = ");
					processValueInline (true, "", exnVal, out);
					out.println ("\"" + Trace.GRAPHVIZ_FRAME_EXCEPTION_ATTRIBUTES + "];");
					if (len != 0) out.println (PREFIX_EXCEPTION + " -> " + frameGvName (len) + "[label=\"\"" + Trace.GRAPHVIZ_FRAME_FRAME_ARROW_ATTRIBUTES + "];");
				} else {
					out.println (PREFIX_EXCEPTION + " [label=\"exception\"" + Trace.GRAPHVIZ_FRAME_EXCEPTION_ATTRIBUTES + "];");
					processValueStandalone (PREFIX_EXCEPTION, Trace.GRAPHVIZ_FRAME_OBJECT_ARROW_ATTRIBUTES, "", exnVal, out, visited);
					if (len != 0) out.println (PREFIX_EXCEPTION + " -> " + frameGvName (len) + "[label=\"\"" + Trace.GRAPHVIZ_FRAME_FRAME_ARROW_ATTRIBUTES + "];");
				}
			}
		});
	}
	public static void drawObjects (String loc, Map<String, ObjectReference> objects) {
		drawStuff (loc, (out) -> {
			Set<ObjectReference> visited = new HashSet<> ();
			for (String key : objects.keySet ()) {
				processObjectWithLabel (key, objects.get (key), out, visited);
			}
		});
	}
	protected static void drawStuff (String loc, Consumer<PrintWriter> consumer) {
		String filenamePrefix = nextFilename ();
		String theLoc = (loc != null && Trace.GRAPHVIZ_PUT_LINE_NUMBER_IN_FILENAME) ? "-" + loc : "";
		File gvFile = new File (filenamePrefix + theLoc + ".gv");
		PrintWriter out;
		try {
			out = new PrintWriter (new FileWriter (gvFile));
		} catch (IOException e) {
			throw new Error ("\n!!!! Cannot open " + gvFile + "for writing");
		}
		out.println ("digraph Java {");
		consumer.accept (out);
		out.println ("}");
		out.close ();
		//System.err.println (gvFile);
		if (Trace.GRAPHVIZ_RUN_GRAPHVIZ) {
			String executable = null;
			for (String s : Trace.GRAPHVIZ_POSSIBLE_DOT_LOCATIONS) {
				if (new File (s).canExecute ()) executable = s;
			}
			if (executable != null) {
				ProcessBuilder pb = new ProcessBuilder (executable, "-T", Trace.GRAPHVIZ_OUTPUT_FORMAT);
				File outFile = new File (filenamePrefix + theLoc + "." + Trace.GRAPHVIZ_OUTPUT_FORMAT);
				pb.redirectInput (gvFile);
				pb.redirectOutput (outFile);
				int result = -1;
				try {
					result = pb.start ().waitFor ();
				} catch (IOException e) {
					throw new Error ("\n!!!! Cannot execute " + executable + "\n!!!! Make sure you have installed http://www.graphviz.org/"
							+ "\n!!!! Check the value of GRAPHVIZ_POSSIBLE_DOT_LOCATIONS in " + Trace.class.getCanonicalName ());
				} catch (InterruptedException e) {
					throw new Error ("\n!!!! Execution of " + executable + "interrupted");
				}
				if (result == 0) {
					if (Trace.GRAPHVIZ_REMOVE_GV_FILES) {
						gvFile.delete ();
					}
				} else {
					outFile.delete ();
				}
			}
		}
	}

	//    public static void drawFrames (int start, String loc, Value returnVal, Value exnVal, List<StackFrame> frames, Set<ReferenceType> staticClasses, Map<String, ObjectReference> objects) {
	//        String filenamePrefix = nextFilename ();
	//        String theLoc = (loc != null && Trace.GRAPHVIZ_PUT_LINE_NUMBER_IN_FILENAME) ? "-" + loc : "";
	//        File gvFile = new File (filenamePrefix + theLoc + ".gv");
	//        PrintWriter out;
	//        try {
	//            out = new PrintWriter (new FileWriter (gvFile));
	//        } catch (IOException e) {
	//            throw new Error ("\n!!!! Cannot open " + gvFile + "for writing");
	//        }
	//        processFrames (start, returnVal, exnVal, frames, staticClasses, objects, out);
	//        out.close ();
	//        //System.err.println (gvFile);
	//        if (Trace.GRAPHVIZ_RUN_DOT) {
	//            String executable = null;
	//            for (String s : Trace.GRAPHVIZ_POSSIBLE_DOT_LOCATIONS) {
	//                if (new File (s).canExecute ())
	//                    executable = s;
	//            }
	//            if (executable != null) {
	//                ProcessBuilder pb = new ProcessBuilder (executable, "-T", Trace.GRAPHVIZ_DOT_OUTPUT_FORMAT);
	//                File outFile = new File (filenamePrefix + theLoc + "." + Trace.GRAPHVIZ_DOT_OUTPUT_FORMAT);
	//                pb.redirectInput (gvFile);
	//                pb.redirectOutput(outFile);
	//                int result = -1;
	//                try {
	//                    result = pb.start ().waitFor ();
	//                } catch (IOException e) {
	//                    throw new Error ("\n!!!! Cannot execute " + executable +
	//                            "\n!!!! Make sure you have installed http://www.graphviz.org/" +
	//                            "\n!!!! Check the value of GRAPHVIZ_DOT_COMMAND in " + Trace.class.getCanonicalName ());
	//                } catch (InterruptedException e) {
	//                    throw new Error ("\n!!!! Execution of " + executable + "interrupted");
	//                }
	//                if (result == 0) {
	//                    if (Trace.GRAPHVIZ_REMOVE_GV_FILES) {
	//                        gvFile.delete ();
	//                    }
	//                } else {
	//                    outFile.delete ();
	//                }
	//            }
	//        }
	//    }
}
