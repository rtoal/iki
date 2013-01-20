[Iki](http://cs.lmu.edu/~ray/notes/iki/) is a small programming language,
which is why it is not named Nui. It is small enough that one can write a
complete, formal semantics for the language, and that students can build a
compiler for the language themselves.

This project is an implementation of an Iki compiler written in Java.
It is a traditional Maven-based project and uses JavaCC for the front-end
and JUnit for testing.  It includes backends for outputting JavaScript, C,
and (non-optimized) assembly language for the x86-64 architecture.

If you are teaching a compiler course, please feel free to have your students
fork this project and implement extensions to the language, and improvements
to the compiler itself.
