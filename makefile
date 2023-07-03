SRCDIR = src
SRC = $(wildcard $(SRCDIR)/**/*.java)
BIN = $(patsubst $(SRCDIR)/%.java, %.class, $(SRC))
PROGRAM = JSOpeN.JSONObject
NAME = JSOpeN.jar
MANIFEST = manifest.txt
ICON = ./bin/ico

build: $(BIN)

run: build
	java $(PROGRAM)

$(BIN) : $(SRC)
	javac -d . $^

jar: build $(MANIFEST) 
	jar cmf $(MANIFEST) $(NAME) **/*.class module-info.java

$(MANIFEST):
	@echo "Manifest-Version: 1.0" > $(MANIFEST)
	@echo "Class-Path: ./" >> $(MANIFEST)
	@echo "" >> $(MANIFEST)

clean:
	rm -f **/*.class
	rm -f $(NAME)
	rm -f manifext.txt
