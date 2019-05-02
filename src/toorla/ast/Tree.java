package toorla.ast;

import toorla.nameAnalyzer.compileErrorException.CompileErrorException;
import toorla.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public abstract class Tree {
	public int line;
	public int col;
	public List<CompileErrorException> relatedErrors = new ArrayList<>();

	public abstract <R> R accept(Visitor<R> visitor);

	public abstract String toString();
}