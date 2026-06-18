# Codecrafters Shell (Java)

A Unix-like shell built from scratch in Java as part of the Codecrafters Shell Challenge.

## Features

### Command Execution

* Execute external programs from the system PATH
* Built-in command support
* PATH-based executable resolution

### Built-in Commands

* `echo`
* `pwd`
* `cd`
* `type`
* `exit`
* `jobs`

### Shell Parsing

* Tokenization of command input
* Single quote support (`'...'`)
* Double quote support (`"..."`)
* Escape sequence handling
* Quoted executable names
* Argument parsing

### Redirection

#### Standard Output

* `>`
* `1>`
* `>>`
* `1>>`

#### Standard Error

* `2>`
* `2>>`

### Background Jobs

* Launch commands with `&`
* Job tracking
* `jobs` builtin
* Automatic job reaping
* Job status updates (`Running`, `Done`)
* Job number recycling

### Pipelines

* Two-command pipelines
* Multi-stage pipelines
* Builtin/external command pipelines

Examples:

```bash
echo hello | wc

cat file.txt | head -n 5 | wc

sleep 100 &

jobs

echo hello > output.txt

cat input.txt | grep error >> logs.txt
```

## Project Structure

```text
src/main/java/
├── Main.java
├── Builtins.java
├── BuiltinExecutor.java
├── Executor.java
├── Parser.java
├── ParsedCommand.java
├── PathResolver.java
├── Tokenizer.java
├── Job.java
├── JobManager.java
└── RedirectUtils.java
```

## What I Learned

Building this shell involved implementing many concepts commonly used by operating systems and command interpreters:

* Process creation and management
* Standard input/output/error streams
* Pipes and inter-process communication
* Background job control
* Command parsing and tokenization
* File descriptor redirection
* PATH resolution
* Shell built-ins

## Technologies

* Java
* Maven
* ProcessBuilder API
* Codecrafters Shell Challenge

## Running

```bash
mvn package

./your_program.sh
```

## Example Session

```bash
$ echo hello world
hello world

$ echo hello > output.txt

$ cat output.txt
hello

$ sleep 100 &
[1] 12345

$ jobs
[1]+ Running                 sleep 100 &

$ cat file.txt | head -n 3 | wc
       3       3      21
```

## Status

Work in progress — currently implementing the Codecrafters Shell challenge stages.
