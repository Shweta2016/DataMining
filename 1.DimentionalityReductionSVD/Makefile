

DIR = .
SRCS = $(wildcard $(DIR)/*.java)
OBJS = $(SRCS:.java=.class)

all:	$(OBJS)

clean: FORCE
	rm -rf *.class core *~

.SUFFIXES: .java .class

.java.class:
	javac $<

FORCE:
