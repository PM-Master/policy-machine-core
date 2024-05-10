// Generated from PMLParser.g4 by ANTLR 4.13.1
package gov.nist.csd.pm.pap.pml.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class PMLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CREATE=1, DELETE=2, POLICY_ELEMENT=3, CONTAINED=4, RULE=5, WHEN=6, PERFORMS=7, 
		AS=8, ON=9, IN=10, DO=11, PATTERN_OP=12, INTERSECTION=13, UNION=14, ANY=15, 
		PROCESS=16, SET_RESOURCE_ACCESS_RIGHTS=17, ASSIGN=18, ASSIGN_TO=19, DEASSIGN=20, 
		FROM=21, SET_PROPERTIES=22, WITH_PROPERTIES=23, OF=24, TO=25, ASSOCIATE=26, 
		AND=27, WITH=28, DISSOCIATE=29, DENY=30, PROHIBITION=31, OBLIGATION=32, 
		ACCESS_RIGHTS=33, POLICY_CLASS=34, OBJECT_ATTRIBUTE=35, USER_ATTRIBUTE=36, 
		USER_ATTRIBUTES=37, OBJECT_ATTRIBUTES=38, OBJECT=39, USER=40, USERS=41, 
		ATTRIBUTE=42, ASSOCIATIONS=43, BREAK=44, DEFAULT=45, FUNCTION=46, INTERFACE=47, 
		MAP=48, ELSE=49, CONST=50, IF=51, RANGE=52, CONTINUE=53, FOREACH=54, RETURN=55, 
		VAR=56, STRING_TYPE=57, BOOL_TYPE=58, VOID_TYPE=59, ARRAY_TYPE=60, NIL_LIT=61, 
		TRUE=62, FALSE=63, ID=64, OPEN_PAREN=65, CLOSE_PAREN=66, OPEN_CURLY=67, 
		CLOSE_CURLY=68, OPEN_BRACKET=69, CLOSE_BRACKET=70, ASSIGN_EQUALS=71, COMMA=72, 
		SEMI=73, COLON=74, DOT=75, DECLARE_ASSIGN=76, LOGICAL_OR=77, LOGICAL_AND=78, 
		EQUALS=79, NOT_EQUALS=80, EXCLAMATION=81, PLUS=82, DOUBLE_QUOTE_STRING=83, 
		WS=84, COMMENT=85, LINE_COMMENT=86;
	public static final int
		RULE_pml = 0, RULE_statement = 1, RULE_statementBlock = 2, RULE_createPolicyStatement = 3, 
		RULE_hierarchy = 4, RULE_userAttrsHierarchy = 5, RULE_objectAttrsHierarchy = 6, 
		RULE_associationsHierarchy = 7, RULE_hierarchyBlock = 8, RULE_associationsHierarchyBlock = 9, 
		RULE_hierarchyStatement = 10, RULE_associationsHierarchyStatement = 11, 
		RULE_createNonPCStatement = 12, RULE_nonPCNodeType = 13, RULE_createObligationStatement = 14, 
		RULE_createRuleStatement = 15, RULE_patternArray = 16, RULE_response = 17, 
		RULE_responseBlock = 18, RULE_responseStatement = 19, RULE_createProhibitionStatement = 20, 
		RULE_setNodePropertiesStatement = 21, RULE_assignStatement = 22, RULE_deassignStatement = 23, 
		RULE_associateStatement = 24, RULE_dissociateStatement = 25, RULE_setResourceAccessRightsStatement = 26, 
		RULE_deleteStatement = 27, RULE_deleteType = 28, RULE_nodeType = 29, RULE_deleteRuleStatement = 30, 
		RULE_variableDeclarationStatement = 31, RULE_constSpec = 32, RULE_varSpec = 33, 
		RULE_variableAssignmentStatement = 34, RULE_functionDefinitionStatement = 35, 
		RULE_functionSignature = 36, RULE_formalArgList = 37, RULE_formalArg = 38, 
		RULE_returnStatement = 39, RULE_functionInvokeStatement = 40, RULE_foreachStatement = 41, 
		RULE_breakStatement = 42, RULE_continueStatement = 43, RULE_ifStatement = 44, 
		RULE_elseIfStatement = 45, RULE_elseStatement = 46, RULE_variableType = 47, 
		RULE_mapType = 48, RULE_arrayType = 49, RULE_expression = 50, RULE_expressionList = 51, 
		RULE_pattern = 52, RULE_literal = 53, RULE_stringLit = 54, RULE_boolLit = 55, 
		RULE_arrayLit = 56, RULE_mapLit = 57, RULE_element = 58, RULE_variableReference = 59, 
		RULE_index = 60, RULE_id = 61, RULE_functionInvoke = 62, RULE_functionInvokeArgs = 63;
	private static String[] makeRuleNames() {
		return new String[] {
			"pml", "statement", "statementBlock", "createPolicyStatement", "hierarchy", 
			"userAttrsHierarchy", "objectAttrsHierarchy", "associationsHierarchy", 
			"hierarchyBlock", "associationsHierarchyBlock", "hierarchyStatement", 
			"associationsHierarchyStatement", "createNonPCStatement", "nonPCNodeType", 
			"createObligationStatement", "createRuleStatement", "patternArray", "response", 
			"responseBlock", "responseStatement", "createProhibitionStatement", "setNodePropertiesStatement", 
			"assignStatement", "deassignStatement", "associateStatement", "dissociateStatement", 
			"setResourceAccessRightsStatement", "deleteStatement", "deleteType", 
			"nodeType", "deleteRuleStatement", "variableDeclarationStatement", "constSpec", 
			"varSpec", "variableAssignmentStatement", "functionDefinitionStatement", 
			"functionSignature", "formalArgList", "formalArg", "returnStatement", 
			"functionInvokeStatement", "foreachStatement", "breakStatement", "continueStatement", 
			"ifStatement", "elseIfStatement", "elseStatement", "variableType", "mapType", 
			"arrayType", "expression", "expressionList", "pattern", "literal", "stringLit", 
			"boolLit", "arrayLit", "mapLit", "element", "variableReference", "index", 
			"id", "functionInvoke", "functionInvokeArgs"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'create'", "'delete'", null, "'contained'", "'rule'", "'when'", 
			"'performs'", "'as'", "'on'", "'in'", "'do'", "'=>'", null, "'union'", 
			"'any'", "'process'", "'set resource access rights'", "'assign'", "'assign to'", 
			"'deassign'", "'from'", "'set properties'", "'with properties'", "'of'", 
			"'to'", "'associate'", "'and'", "'with'", "'dissociate'", "'deny'", "'prohibition'", 
			"'obligation'", "'access rights'", null, null, null, null, null, null, 
			null, null, "'attribute'", "'associations'", "'break'", "'default'", 
			"'function'", "'interface'", "'map'", "'else'", "'const'", "'if'", "'range'", 
			"'continue'", "'foreach'", "'return'", "'var'", "'string'", "'bool'", 
			"'void'", "'array'", "'nil'", "'true'", "'false'", null, "'('", "')'", 
			"'{'", "'}'", "'['", "']'", "'='", "','", "';'", "':'", "'.'", "':='", 
			"'||'", "'&&'", "'=='", "'!='", "'!'", "'+'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "CREATE", "DELETE", "POLICY_ELEMENT", "CONTAINED", "RULE", "WHEN", 
			"PERFORMS", "AS", "ON", "IN", "DO", "PATTERN_OP", "INTERSECTION", "UNION", 
			"ANY", "PROCESS", "SET_RESOURCE_ACCESS_RIGHTS", "ASSIGN", "ASSIGN_TO", 
			"DEASSIGN", "FROM", "SET_PROPERTIES", "WITH_PROPERTIES", "OF", "TO", 
			"ASSOCIATE", "AND", "WITH", "DISSOCIATE", "DENY", "PROHIBITION", "OBLIGATION", 
			"ACCESS_RIGHTS", "POLICY_CLASS", "OBJECT_ATTRIBUTE", "USER_ATTRIBUTE", 
			"USER_ATTRIBUTES", "OBJECT_ATTRIBUTES", "OBJECT", "USER", "USERS", "ATTRIBUTE", 
			"ASSOCIATIONS", "BREAK", "DEFAULT", "FUNCTION", "INTERFACE", "MAP", "ELSE", 
			"CONST", "IF", "RANGE", "CONTINUE", "FOREACH", "RETURN", "VAR", "STRING_TYPE", 
			"BOOL_TYPE", "VOID_TYPE", "ARRAY_TYPE", "NIL_LIT", "TRUE", "FALSE", "ID", 
			"OPEN_PAREN", "CLOSE_PAREN", "OPEN_CURLY", "CLOSE_CURLY", "OPEN_BRACKET", 
			"CLOSE_BRACKET", "ASSIGN_EQUALS", "COMMA", "SEMI", "COLON", "DOT", "DECLARE_ASSIGN", 
			"LOGICAL_OR", "LOGICAL_AND", "EQUALS", "NOT_EQUALS", "EXCLAMATION", "PLUS", 
			"DOUBLE_QUOTE_STRING", "WS", "COMMENT", "LINE_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "PMLParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PMLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PmlContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(PMLParser.EOF, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public PmlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pml; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterPml(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitPml(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitPml(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PmlContext pml() throws RecognitionException {
		PmlContext _localctx = new PmlContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_pml);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & -9154085211814035453L) != 0)) {
				{
				{
				setState(128);
				statement();
				}
				}
				setState(133);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(134);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public CreatePolicyStatementContext createPolicyStatement() {
			return getRuleContext(CreatePolicyStatementContext.class,0);
		}
		public CreateNonPCStatementContext createNonPCStatement() {
			return getRuleContext(CreateNonPCStatementContext.class,0);
		}
		public CreateObligationStatementContext createObligationStatement() {
			return getRuleContext(CreateObligationStatementContext.class,0);
		}
		public CreateProhibitionStatementContext createProhibitionStatement() {
			return getRuleContext(CreateProhibitionStatementContext.class,0);
		}
		public SetNodePropertiesStatementContext setNodePropertiesStatement() {
			return getRuleContext(SetNodePropertiesStatementContext.class,0);
		}
		public AssignStatementContext assignStatement() {
			return getRuleContext(AssignStatementContext.class,0);
		}
		public DeassignStatementContext deassignStatement() {
			return getRuleContext(DeassignStatementContext.class,0);
		}
		public AssociateStatementContext associateStatement() {
			return getRuleContext(AssociateStatementContext.class,0);
		}
		public DissociateStatementContext dissociateStatement() {
			return getRuleContext(DissociateStatementContext.class,0);
		}
		public SetResourceAccessRightsStatementContext setResourceAccessRightsStatement() {
			return getRuleContext(SetResourceAccessRightsStatementContext.class,0);
		}
		public DeleteStatementContext deleteStatement() {
			return getRuleContext(DeleteStatementContext.class,0);
		}
		public DeleteRuleStatementContext deleteRuleStatement() {
			return getRuleContext(DeleteRuleStatementContext.class,0);
		}
		public VariableAssignmentStatementContext variableAssignmentStatement() {
			return getRuleContext(VariableAssignmentStatementContext.class,0);
		}
		public VariableDeclarationStatementContext variableDeclarationStatement() {
			return getRuleContext(VariableDeclarationStatementContext.class,0);
		}
		public FunctionDefinitionStatementContext functionDefinitionStatement() {
			return getRuleContext(FunctionDefinitionStatementContext.class,0);
		}
		public ReturnStatementContext returnStatement() {
			return getRuleContext(ReturnStatementContext.class,0);
		}
		public ForeachStatementContext foreachStatement() {
			return getRuleContext(ForeachStatementContext.class,0);
		}
		public BreakStatementContext breakStatement() {
			return getRuleContext(BreakStatementContext.class,0);
		}
		public ContinueStatementContext continueStatement() {
			return getRuleContext(ContinueStatementContext.class,0);
		}
		public FunctionInvokeStatementContext functionInvokeStatement() {
			return getRuleContext(FunctionInvokeStatementContext.class,0);
		}
		public IfStatementContext ifStatement() {
			return getRuleContext(IfStatementContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(136);
				createPolicyStatement();
				}
				break;
			case 2:
				{
				setState(137);
				createNonPCStatement();
				}
				break;
			case 3:
				{
				setState(138);
				createObligationStatement();
				}
				break;
			case 4:
				{
				setState(139);
				createProhibitionStatement();
				}
				break;
			case 5:
				{
				setState(140);
				setNodePropertiesStatement();
				}
				break;
			case 6:
				{
				setState(141);
				assignStatement();
				}
				break;
			case 7:
				{
				setState(142);
				deassignStatement();
				}
				break;
			case 8:
				{
				setState(143);
				associateStatement();
				}
				break;
			case 9:
				{
				setState(144);
				dissociateStatement();
				}
				break;
			case 10:
				{
				setState(145);
				setResourceAccessRightsStatement();
				}
				break;
			case 11:
				{
				setState(146);
				deleteStatement();
				}
				break;
			case 12:
				{
				setState(147);
				deleteRuleStatement();
				}
				break;
			case 13:
				{
				setState(148);
				variableAssignmentStatement();
				}
				break;
			case 14:
				{
				setState(149);
				variableDeclarationStatement();
				}
				break;
			case 15:
				{
				setState(150);
				functionDefinitionStatement();
				}
				break;
			case 16:
				{
				setState(151);
				returnStatement();
				}
				break;
			case 17:
				{
				setState(152);
				foreachStatement();
				}
				break;
			case 18:
				{
				setState(153);
				breakStatement();
				}
				break;
			case 19:
				{
				setState(154);
				continueStatement();
				}
				break;
			case 20:
				{
				setState(155);
				functionInvokeStatement();
				}
				break;
			case 21:
				{
				setState(156);
				ifStatement();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatementBlockContext extends ParserRuleContext {
		public TerminalNode OPEN_CURLY() { return getToken(PMLParser.OPEN_CURLY, 0); }
		public TerminalNode CLOSE_CURLY() { return getToken(PMLParser.CLOSE_CURLY, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public StatementBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterStatementBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitStatementBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitStatementBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementBlockContext statementBlock() throws RecognitionException {
		StatementBlockContext _localctx = new StatementBlockContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_statementBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(OPEN_CURLY);
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & -9154085211814035453L) != 0)) {
				{
				{
				setState(160);
				statement();
				}
				}
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(166);
			match(CLOSE_CURLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CreatePolicyStatementContext extends ParserRuleContext {
		public ExpressionContext name;
		public ExpressionContext properties;
		public TerminalNode CREATE() { return getToken(PMLParser.CREATE, 0); }
		public TerminalNode POLICY_CLASS() { return getToken(PMLParser.POLICY_CLASS, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode WITH_PROPERTIES() { return getToken(PMLParser.WITH_PROPERTIES, 0); }
		public HierarchyContext hierarchy() {
			return getRuleContext(HierarchyContext.class,0);
		}
		public CreatePolicyStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createPolicyStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterCreatePolicyStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitCreatePolicyStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitCreatePolicyStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreatePolicyStatementContext createPolicyStatement() throws RecognitionException {
		CreatePolicyStatementContext _localctx = new CreatePolicyStatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_createPolicyStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			match(CREATE);
			setState(169);
			match(POLICY_CLASS);
			setState(170);
			((CreatePolicyStatementContext)_localctx).name = expression(0);
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WITH_PROPERTIES) {
				{
				setState(171);
				match(WITH_PROPERTIES);
				setState(172);
				((CreatePolicyStatementContext)_localctx).properties = expression(0);
				}
			}

			setState(176);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_CURLY) {
				{
				setState(175);
				hierarchy();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HierarchyContext extends ParserRuleContext {
		public TerminalNode OPEN_CURLY() { return getToken(PMLParser.OPEN_CURLY, 0); }
		public TerminalNode CLOSE_CURLY() { return getToken(PMLParser.CLOSE_CURLY, 0); }
		public UserAttrsHierarchyContext userAttrsHierarchy() {
			return getRuleContext(UserAttrsHierarchyContext.class,0);
		}
		public ObjectAttrsHierarchyContext objectAttrsHierarchy() {
			return getRuleContext(ObjectAttrsHierarchyContext.class,0);
		}
		public AssociationsHierarchyContext associationsHierarchy() {
			return getRuleContext(AssociationsHierarchyContext.class,0);
		}
		public HierarchyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hierarchy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterHierarchy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitHierarchy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitHierarchy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HierarchyContext hierarchy() throws RecognitionException {
		HierarchyContext _localctx = new HierarchyContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_hierarchy);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			match(OPEN_CURLY);
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==USER_ATTRIBUTES) {
				{
				setState(179);
				userAttrsHierarchy();
				}
			}

			setState(183);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OBJECT_ATTRIBUTES) {
				{
				setState(182);
				objectAttrsHierarchy();
				}
			}

			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASSOCIATIONS) {
				{
				setState(185);
				associationsHierarchy();
				}
			}

			setState(188);
			match(CLOSE_CURLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UserAttrsHierarchyContext extends ParserRuleContext {
		public TerminalNode USER_ATTRIBUTES() { return getToken(PMLParser.USER_ATTRIBUTES, 0); }
		public HierarchyBlockContext hierarchyBlock() {
			return getRuleContext(HierarchyBlockContext.class,0);
		}
		public UserAttrsHierarchyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_userAttrsHierarchy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterUserAttrsHierarchy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitUserAttrsHierarchy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitUserAttrsHierarchy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UserAttrsHierarchyContext userAttrsHierarchy() throws RecognitionException {
		UserAttrsHierarchyContext _localctx = new UserAttrsHierarchyContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_userAttrsHierarchy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			match(USER_ATTRIBUTES);
			setState(191);
			hierarchyBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectAttrsHierarchyContext extends ParserRuleContext {
		public TerminalNode OBJECT_ATTRIBUTES() { return getToken(PMLParser.OBJECT_ATTRIBUTES, 0); }
		public HierarchyBlockContext hierarchyBlock() {
			return getRuleContext(HierarchyBlockContext.class,0);
		}
		public ObjectAttrsHierarchyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectAttrsHierarchy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterObjectAttrsHierarchy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitObjectAttrsHierarchy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitObjectAttrsHierarchy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectAttrsHierarchyContext objectAttrsHierarchy() throws RecognitionException {
		ObjectAttrsHierarchyContext _localctx = new ObjectAttrsHierarchyContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_objectAttrsHierarchy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			match(OBJECT_ATTRIBUTES);
			setState(194);
			hierarchyBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssociationsHierarchyContext extends ParserRuleContext {
		public TerminalNode ASSOCIATIONS() { return getToken(PMLParser.ASSOCIATIONS, 0); }
		public AssociationsHierarchyBlockContext associationsHierarchyBlock() {
			return getRuleContext(AssociationsHierarchyBlockContext.class,0);
		}
		public AssociationsHierarchyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_associationsHierarchy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterAssociationsHierarchy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitAssociationsHierarchy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitAssociationsHierarchy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssociationsHierarchyContext associationsHierarchy() throws RecognitionException {
		AssociationsHierarchyContext _localctx = new AssociationsHierarchyContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_associationsHierarchy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(196);
			match(ASSOCIATIONS);
			setState(197);
			associationsHierarchyBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HierarchyBlockContext extends ParserRuleContext {
		public TerminalNode OPEN_CURLY() { return getToken(PMLParser.OPEN_CURLY, 0); }
		public TerminalNode CLOSE_CURLY() { return getToken(PMLParser.CLOSE_CURLY, 0); }
		public List<HierarchyStatementContext> hierarchyStatement() {
			return getRuleContexts(HierarchyStatementContext.class);
		}
		public HierarchyStatementContext hierarchyStatement(int i) {
			return getRuleContext(HierarchyStatementContext.class,i);
		}
		public HierarchyBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hierarchyBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterHierarchyBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitHierarchyBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitHierarchyBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HierarchyBlockContext hierarchyBlock() throws RecognitionException {
		HierarchyBlockContext _localctx = new HierarchyBlockContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_hierarchyBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			match(OPEN_CURLY);
			setState(203);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & 2621615L) != 0)) {
				{
				{
				setState(200);
				hierarchyStatement();
				}
				}
				setState(205);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(206);
			match(CLOSE_CURLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssociationsHierarchyBlockContext extends ParserRuleContext {
		public TerminalNode OPEN_CURLY() { return getToken(PMLParser.OPEN_CURLY, 0); }
		public TerminalNode CLOSE_CURLY() { return getToken(PMLParser.CLOSE_CURLY, 0); }
		public List<AssociationsHierarchyStatementContext> associationsHierarchyStatement() {
			return getRuleContexts(AssociationsHierarchyStatementContext.class);
		}
		public AssociationsHierarchyStatementContext associationsHierarchyStatement(int i) {
			return getRuleContext(AssociationsHierarchyStatementContext.class,i);
		}
		public AssociationsHierarchyBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_associationsHierarchyBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterAssociationsHierarchyBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitAssociationsHierarchyBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitAssociationsHierarchyBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssociationsHierarchyBlockContext associationsHierarchyBlock() throws RecognitionException {
		AssociationsHierarchyBlockContext _localctx = new AssociationsHierarchyBlockContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_associationsHierarchyBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			match(OPEN_CURLY);
			setState(212);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & 2621615L) != 0)) {
				{
				{
				setState(209);
				associationsHierarchyStatement();
				}
				}
				setState(214);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(215);
			match(CLOSE_CURLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HierarchyStatementContext extends ParserRuleContext {
		public ExpressionContext name;
		public ExpressionContext properties;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public HierarchyStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hierarchyStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterHierarchyStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitHierarchyStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitHierarchyStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HierarchyStatementContext hierarchyStatement() throws RecognitionException {
		HierarchyStatementContext _localctx = new HierarchyStatementContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_hierarchyStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			((HierarchyStatementContext)_localctx).name = expression(0);
			setState(219);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(218);
				((HierarchyStatementContext)_localctx).properties = expression(0);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssociationsHierarchyStatementContext extends ParserRuleContext {
		public ExpressionContext ua;
		public ExpressionContext target;
		public ExpressionContext arset;
		public TerminalNode AND() { return getToken(PMLParser.AND, 0); }
		public TerminalNode WITH() { return getToken(PMLParser.WITH, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public AssociationsHierarchyStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_associationsHierarchyStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterAssociationsHierarchyStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitAssociationsHierarchyStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitAssociationsHierarchyStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssociationsHierarchyStatementContext associationsHierarchyStatement() throws RecognitionException {
		AssociationsHierarchyStatementContext _localctx = new AssociationsHierarchyStatementContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_associationsHierarchyStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
			((AssociationsHierarchyStatementContext)_localctx).ua = expression(0);
			setState(222);
			match(AND);
			setState(223);
			((AssociationsHierarchyStatementContext)_localctx).target = expression(0);
			setState(224);
			match(WITH);
			setState(225);
			((AssociationsHierarchyStatementContext)_localctx).arset = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CreateNonPCStatementContext extends ParserRuleContext {
		public ExpressionContext name;
		public ExpressionContext properties;
		public ExpressionContext assignTo;
		public TerminalNode CREATE() { return getToken(PMLParser.CREATE, 0); }
		public NonPCNodeTypeContext nonPCNodeType() {
			return getRuleContext(NonPCNodeTypeContext.class,0);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode WITH_PROPERTIES() { return getToken(PMLParser.WITH_PROPERTIES, 0); }
		public TerminalNode ASSIGN_TO() { return getToken(PMLParser.ASSIGN_TO, 0); }
		public CreateNonPCStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createNonPCStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterCreateNonPCStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitCreateNonPCStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitCreateNonPCStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateNonPCStatementContext createNonPCStatement() throws RecognitionException {
		CreateNonPCStatementContext _localctx = new CreateNonPCStatementContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_createNonPCStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
			match(CREATE);
			setState(228);
			nonPCNodeType();
			setState(229);
			((CreateNonPCStatementContext)_localctx).name = expression(0);
			setState(232);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WITH_PROPERTIES) {
				{
				setState(230);
				match(WITH_PROPERTIES);
				setState(231);
				((CreateNonPCStatementContext)_localctx).properties = expression(0);
				}
			}

			setState(236);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASSIGN_TO) {
				{
				setState(234);
				match(ASSIGN_TO);
				setState(235);
				((CreateNonPCStatementContext)_localctx).assignTo = expression(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NonPCNodeTypeContext extends ParserRuleContext {
		public TerminalNode OBJECT_ATTRIBUTE() { return getToken(PMLParser.OBJECT_ATTRIBUTE, 0); }
		public TerminalNode USER_ATTRIBUTE() { return getToken(PMLParser.USER_ATTRIBUTE, 0); }
		public TerminalNode OBJECT() { return getToken(PMLParser.OBJECT, 0); }
		public TerminalNode USER() { return getToken(PMLParser.USER, 0); }
		public NonPCNodeTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonPCNodeType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterNonPCNodeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitNonPCNodeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitNonPCNodeType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NonPCNodeTypeContext nonPCNodeType() throws RecognitionException {
		NonPCNodeTypeContext _localctx = new NonPCNodeTypeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_nonPCNodeType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(238);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1752346656768L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CreateObligationStatementContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(PMLParser.CREATE, 0); }
		public TerminalNode OBLIGATION() { return getToken(PMLParser.OBLIGATION, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode OPEN_CURLY() { return getToken(PMLParser.OPEN_CURLY, 0); }
		public TerminalNode CLOSE_CURLY() { return getToken(PMLParser.CLOSE_CURLY, 0); }
		public List<CreateRuleStatementContext> createRuleStatement() {
			return getRuleContexts(CreateRuleStatementContext.class);
		}
		public CreateRuleStatementContext createRuleStatement(int i) {
			return getRuleContext(CreateRuleStatementContext.class,i);
		}
		public CreateObligationStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createObligationStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterCreateObligationStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitCreateObligationStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitCreateObligationStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateObligationStatementContext createObligationStatement() throws RecognitionException {
		CreateObligationStatementContext _localctx = new CreateObligationStatementContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_createObligationStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			match(CREATE);
			setState(241);
			match(OBLIGATION);
			setState(242);
			expression(0);
			setState(243);
			match(OPEN_CURLY);
			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CREATE) {
				{
				{
				setState(244);
				createRuleStatement();
				}
				}
				setState(249);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(250);
			match(CLOSE_CURLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CreateRuleStatementContext extends ParserRuleContext {
		public ExpressionContext ruleName;
		public PatternContext subjectPattern;
		public PatternContext operationPattern;
		public PatternArrayContext operandPatterns;
		public TerminalNode CREATE() { return getToken(PMLParser.CREATE, 0); }
		public TerminalNode RULE() { return getToken(PMLParser.RULE, 0); }
		public TerminalNode WHEN() { return getToken(PMLParser.WHEN, 0); }
		public TerminalNode PERFORMS() { return getToken(PMLParser.PERFORMS, 0); }
		public ResponseContext response() {
			return getRuleContext(ResponseContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<PatternContext> pattern() {
			return getRuleContexts(PatternContext.class);
		}
		public PatternContext pattern(int i) {
			return getRuleContext(PatternContext.class,i);
		}
		public TerminalNode ON() { return getToken(PMLParser.ON, 0); }
		public PatternArrayContext patternArray() {
			return getRuleContext(PatternArrayContext.class,0);
		}
		public CreateRuleStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createRuleStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterCreateRuleStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitCreateRuleStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitCreateRuleStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateRuleStatementContext createRuleStatement() throws RecognitionException {
		CreateRuleStatementContext _localctx = new CreateRuleStatementContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_createRuleStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(252);
			match(CREATE);
			setState(253);
			match(RULE);
			setState(254);
			((CreateRuleStatementContext)_localctx).ruleName = expression(0);
			setState(255);
			match(WHEN);
			setState(256);
			((CreateRuleStatementContext)_localctx).subjectPattern = pattern();
			setState(257);
			match(PERFORMS);
			setState(258);
			((CreateRuleStatementContext)_localctx).operationPattern = pattern();
			setState(261);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ON) {
				{
				setState(259);
				match(ON);
				setState(260);
				((CreateRuleStatementContext)_localctx).operandPatterns = patternArray();
				}
			}

			setState(263);
			response();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PatternArrayContext extends ParserRuleContext {
		public List<PatternContext> pattern() {
			return getRuleContexts(PatternContext.class);
		}
		public PatternContext pattern(int i) {
			return getRuleContext(PatternContext.class,i);
		}
		public PatternArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_patternArray; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterPatternArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitPatternArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitPatternArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PatternArrayContext patternArray() throws RecognitionException {
		PatternArrayContext _localctx = new PatternArrayContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_patternArray);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(268);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ID || _la==OPEN_PAREN) {
				{
				{
				setState(265);
				pattern();
				}
				}
				setState(270);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ResponseContext extends ParserRuleContext {
		public TerminalNode DO() { return getToken(PMLParser.DO, 0); }
		public TerminalNode OPEN_PAREN() { return getToken(PMLParser.OPEN_PAREN, 0); }
		public TerminalNode ID() { return getToken(PMLParser.ID, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(PMLParser.CLOSE_PAREN, 0); }
		public ResponseBlockContext responseBlock() {
			return getRuleContext(ResponseBlockContext.class,0);
		}
		public ResponseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_response; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterResponse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitResponse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitResponse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ResponseContext response() throws RecognitionException {
		ResponseContext _localctx = new ResponseContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_response);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271);
			match(DO);
			setState(272);
			match(OPEN_PAREN);
			setState(273);
			match(ID);
			setState(274);
			match(CLOSE_PAREN);
			setState(275);
			responseBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ResponseBlockContext extends ParserRuleContext {
		public TerminalNode OPEN_CURLY() { return getToken(PMLParser.OPEN_CURLY, 0); }
		public TerminalNode CLOSE_CURLY() { return getToken(PMLParser.CLOSE_CURLY, 0); }
		public List<ResponseStatementContext> responseStatement() {
			return getRuleContexts(ResponseStatementContext.class);
		}
		public ResponseStatementContext responseStatement(int i) {
			return getRuleContext(ResponseStatementContext.class,i);
		}
		public ResponseBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_responseBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterResponseBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitResponseBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitResponseBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ResponseBlockContext responseBlock() throws RecognitionException {
		ResponseBlockContext _localctx = new ResponseBlockContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_responseBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			match(OPEN_CURLY);
			setState(281);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 1)) & ~0x3f) == 0 && ((1L << (_la - 1)) & -9154085211814035453L) != 0)) {
				{
				{
				setState(278);
				responseStatement();
				}
				}
				setState(283);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(284);
			match(CLOSE_CURLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ResponseStatementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public CreateRuleStatementContext createRuleStatement() {
			return getRuleContext(CreateRuleStatementContext.class,0);
		}
		public DeleteRuleStatementContext deleteRuleStatement() {
			return getRuleContext(DeleteRuleStatementContext.class,0);
		}
		public ResponseStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_responseStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterResponseStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitResponseStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitResponseStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ResponseStatementContext responseStatement() throws RecognitionException {
		ResponseStatementContext _localctx = new ResponseStatementContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_responseStatement);
		try {
			setState(289);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(286);
				statement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(287);
				createRuleStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(288);
				deleteRuleStatement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CreateProhibitionStatementContext extends ParserRuleContext {
		public ExpressionContext name;
		public ExpressionContext subject;
		public ExpressionContext accessRights;
		public ExpressionContext containers;
		public TerminalNode CREATE() { return getToken(PMLParser.CREATE, 0); }
		public TerminalNode PROHIBITION() { return getToken(PMLParser.PROHIBITION, 0); }
		public TerminalNode DENY() { return getToken(PMLParser.DENY, 0); }
		public TerminalNode ACCESS_RIGHTS() { return getToken(PMLParser.ACCESS_RIGHTS, 0); }
		public TerminalNode ON() { return getToken(PMLParser.ON, 0); }
		public TerminalNode OF() { return getToken(PMLParser.OF, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode USER() { return getToken(PMLParser.USER, 0); }
		public TerminalNode USER_ATTRIBUTE() { return getToken(PMLParser.USER_ATTRIBUTE, 0); }
		public TerminalNode PROCESS() { return getToken(PMLParser.PROCESS, 0); }
		public TerminalNode INTERSECTION() { return getToken(PMLParser.INTERSECTION, 0); }
		public TerminalNode UNION() { return getToken(PMLParser.UNION, 0); }
		public CreateProhibitionStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createProhibitionStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterCreateProhibitionStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitCreateProhibitionStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitCreateProhibitionStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateProhibitionStatementContext createProhibitionStatement() throws RecognitionException {
		CreateProhibitionStatementContext _localctx = new CreateProhibitionStatementContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_createProhibitionStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			match(CREATE);
			setState(292);
			match(PROHIBITION);
			setState(293);
			((CreateProhibitionStatementContext)_localctx).name = expression(0);
			setState(294);
			match(DENY);
			setState(295);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1168231170048L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(296);
			((CreateProhibitionStatementContext)_localctx).subject = expression(0);
			setState(297);
			match(ACCESS_RIGHTS);
			setState(298);
			((CreateProhibitionStatementContext)_localctx).accessRights = expression(0);
			setState(299);
			match(ON);
			setState(300);
			_la = _input.LA(1);
			if ( !(_la==INTERSECTION || _la==UNION) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(301);
			match(OF);
			setState(302);
			((CreateProhibitionStatementContext)_localctx).containers = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SetNodePropertiesStatementContext extends ParserRuleContext {
		public ExpressionContext name;
		public ExpressionContext properties;
		public TerminalNode SET_PROPERTIES() { return getToken(PMLParser.SET_PROPERTIES, 0); }
		public TerminalNode OF() { return getToken(PMLParser.OF, 0); }
		public TerminalNode TO() { return getToken(PMLParser.TO, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public SetNodePropertiesStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setNodePropertiesStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterSetNodePropertiesStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitSetNodePropertiesStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitSetNodePropertiesStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SetNodePropertiesStatementContext setNodePropertiesStatement() throws RecognitionException {
		SetNodePropertiesStatementContext _localctx = new SetNodePropertiesStatementContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_setNodePropertiesStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			match(SET_PROPERTIES);
			setState(305);
			match(OF);
			setState(306);
			((SetNodePropertiesStatementContext)_localctx).name = expression(0);
			setState(307);
			match(TO);
			setState(308);
			((SetNodePropertiesStatementContext)_localctx).properties = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssignStatementContext extends ParserRuleContext {
		public ExpressionContext childNode;
		public ExpressionContext parentNodes;
		public TerminalNode ASSIGN() { return getToken(PMLParser.ASSIGN, 0); }
		public TerminalNode TO() { return getToken(PMLParser.TO, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public AssignStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterAssignStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitAssignStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitAssignStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignStatementContext assignStatement() throws RecognitionException {
		AssignStatementContext _localctx = new AssignStatementContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_assignStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(310);
			match(ASSIGN);
			setState(311);
			((AssignStatementContext)_localctx).childNode = expression(0);
			setState(312);
			match(TO);
			setState(313);
			((AssignStatementContext)_localctx).parentNodes = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeassignStatementContext extends ParserRuleContext {
		public ExpressionContext childNode;
		public ExpressionContext parentNodes;
		public TerminalNode DEASSIGN() { return getToken(PMLParser.DEASSIGN, 0); }
		public TerminalNode FROM() { return getToken(PMLParser.FROM, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public DeassignStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_deassignStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterDeassignStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitDeassignStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitDeassignStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeassignStatementContext deassignStatement() throws RecognitionException {
		DeassignStatementContext _localctx = new DeassignStatementContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_deassignStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(315);
			match(DEASSIGN);
			setState(316);
			((DeassignStatementContext)_localctx).childNode = expression(0);
			setState(317);
			match(FROM);
			setState(318);
			((DeassignStatementContext)_localctx).parentNodes = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssociateStatementContext extends ParserRuleContext {
		public ExpressionContext ua;
		public ExpressionContext target;
		public ExpressionContext accessRights;
		public TerminalNode ASSOCIATE() { return getToken(PMLParser.ASSOCIATE, 0); }
		public TerminalNode AND() { return getToken(PMLParser.AND, 0); }
		public TerminalNode WITH() { return getToken(PMLParser.WITH, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public AssociateStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_associateStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterAssociateStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitAssociateStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitAssociateStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssociateStatementContext associateStatement() throws RecognitionException {
		AssociateStatementContext _localctx = new AssociateStatementContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_associateStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(320);
			match(ASSOCIATE);
			setState(321);
			((AssociateStatementContext)_localctx).ua = expression(0);
			setState(322);
			match(AND);
			setState(323);
			((AssociateStatementContext)_localctx).target = expression(0);
			setState(324);
			match(WITH);
			setState(325);
			((AssociateStatementContext)_localctx).accessRights = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DissociateStatementContext extends ParserRuleContext {
		public ExpressionContext ua;
		public ExpressionContext target;
		public TerminalNode DISSOCIATE() { return getToken(PMLParser.DISSOCIATE, 0); }
		public TerminalNode AND() { return getToken(PMLParser.AND, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public DissociateStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dissociateStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterDissociateStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitDissociateStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitDissociateStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DissociateStatementContext dissociateStatement() throws RecognitionException {
		DissociateStatementContext _localctx = new DissociateStatementContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_dissociateStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			match(DISSOCIATE);
			setState(328);
			((DissociateStatementContext)_localctx).ua = expression(0);
			setState(329);
			match(AND);
			setState(330);
			((DissociateStatementContext)_localctx).target = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SetResourceAccessRightsStatementContext extends ParserRuleContext {
		public ExpressionContext accessRights;
		public TerminalNode SET_RESOURCE_ACCESS_RIGHTS() { return getToken(PMLParser.SET_RESOURCE_ACCESS_RIGHTS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SetResourceAccessRightsStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setResourceAccessRightsStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterSetResourceAccessRightsStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitSetResourceAccessRightsStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitSetResourceAccessRightsStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SetResourceAccessRightsStatementContext setResourceAccessRightsStatement() throws RecognitionException {
		SetResourceAccessRightsStatementContext _localctx = new SetResourceAccessRightsStatementContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_setResourceAccessRightsStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(332);
			match(SET_RESOURCE_ACCESS_RIGHTS);
			setState(333);
			((SetResourceAccessRightsStatementContext)_localctx).accessRights = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeleteStatementContext extends ParserRuleContext {
		public TerminalNode DELETE() { return getToken(PMLParser.DELETE, 0); }
		public DeleteTypeContext deleteType() {
			return getRuleContext(DeleteTypeContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public DeleteStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_deleteStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterDeleteStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitDeleteStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitDeleteStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeleteStatementContext deleteStatement() throws RecognitionException {
		DeleteStatementContext _localctx = new DeleteStatementContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_deleteStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			match(DELETE);
			setState(336);
			deleteType();
			setState(337);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeleteTypeContext extends ParserRuleContext {
		public DeleteTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_deleteType; }
	 
		public DeleteTypeContext() { }
		public void copyFrom(DeleteTypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DeleteNodeContext extends DeleteTypeContext {
		public NodeTypeContext nodeType() {
			return getRuleContext(NodeTypeContext.class,0);
		}
		public DeleteNodeContext(DeleteTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterDeleteNode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitDeleteNode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitDeleteNode(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DeleteProhibitionContext extends DeleteTypeContext {
		public TerminalNode PROHIBITION() { return getToken(PMLParser.PROHIBITION, 0); }
		public DeleteProhibitionContext(DeleteTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterDeleteProhibition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitDeleteProhibition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitDeleteProhibition(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DeleteConstContext extends DeleteTypeContext {
		public TerminalNode CONST() { return getToken(PMLParser.CONST, 0); }
		public DeleteConstContext(DeleteTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterDeleteConst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitDeleteConst(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitDeleteConst(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DeleteFunctionContext extends DeleteTypeContext {
		public TerminalNode FUNCTION() { return getToken(PMLParser.FUNCTION, 0); }
		public DeleteFunctionContext(DeleteTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterDeleteFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitDeleteFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitDeleteFunction(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DeleteObligationContext extends DeleteTypeContext {
		public TerminalNode OBLIGATION() { return getToken(PMLParser.OBLIGATION, 0); }
		public DeleteObligationContext(DeleteTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterDeleteObligation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitDeleteObligation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitDeleteObligation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeleteTypeContext deleteType() throws RecognitionException {
		DeleteTypeContext _localctx = new DeleteTypeContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_deleteType);
		try {
			setState(344);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case POLICY_CLASS:
			case OBJECT_ATTRIBUTE:
			case USER_ATTRIBUTE:
			case OBJECT:
			case USER:
				_localctx = new DeleteNodeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(339);
				nodeType();
				}
				break;
			case OBLIGATION:
				_localctx = new DeleteObligationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(340);
				match(OBLIGATION);
				}
				break;
			case PROHIBITION:
				_localctx = new DeleteProhibitionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(341);
				match(PROHIBITION);
				}
				break;
			case FUNCTION:
				_localctx = new DeleteFunctionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(342);
				match(FUNCTION);
				}
				break;
			case CONST:
				_localctx = new DeleteConstContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(343);
				match(CONST);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NodeTypeContext extends ParserRuleContext {
		public TerminalNode POLICY_CLASS() { return getToken(PMLParser.POLICY_CLASS, 0); }
		public TerminalNode OBJECT_ATTRIBUTE() { return getToken(PMLParser.OBJECT_ATTRIBUTE, 0); }
		public TerminalNode USER_ATTRIBUTE() { return getToken(PMLParser.USER_ATTRIBUTE, 0); }
		public TerminalNode OBJECT() { return getToken(PMLParser.OBJECT, 0); }
		public TerminalNode USER() { return getToken(PMLParser.USER, 0); }
		public NodeTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nodeType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterNodeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitNodeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitNodeType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NodeTypeContext nodeType() throws RecognitionException {
		NodeTypeContext _localctx = new NodeTypeContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_nodeType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(346);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1769526525952L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeleteRuleStatementContext extends ParserRuleContext {
		public ExpressionContext ruleName;
		public ExpressionContext obligationName;
		public TerminalNode DELETE() { return getToken(PMLParser.DELETE, 0); }
		public TerminalNode RULE() { return getToken(PMLParser.RULE, 0); }
		public TerminalNode FROM() { return getToken(PMLParser.FROM, 0); }
		public TerminalNode OBLIGATION() { return getToken(PMLParser.OBLIGATION, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public DeleteRuleStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_deleteRuleStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterDeleteRuleStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitDeleteRuleStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitDeleteRuleStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeleteRuleStatementContext deleteRuleStatement() throws RecognitionException {
		DeleteRuleStatementContext _localctx = new DeleteRuleStatementContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_deleteRuleStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(348);
			match(DELETE);
			setState(349);
			match(RULE);
			setState(350);
			((DeleteRuleStatementContext)_localctx).ruleName = expression(0);
			setState(351);
			match(FROM);
			setState(352);
			match(OBLIGATION);
			setState(353);
			((DeleteRuleStatementContext)_localctx).obligationName = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableDeclarationStatementContext extends ParserRuleContext {
		public VariableDeclarationStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclarationStatement; }
	 
		public VariableDeclarationStatementContext() { }
		public void copyFrom(VariableDeclarationStatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class VarDeclarationContext extends VariableDeclarationStatementContext {
		public TerminalNode VAR() { return getToken(PMLParser.VAR, 0); }
		public List<VarSpecContext> varSpec() {
			return getRuleContexts(VarSpecContext.class);
		}
		public VarSpecContext varSpec(int i) {
			return getRuleContext(VarSpecContext.class,i);
		}
		public TerminalNode OPEN_PAREN() { return getToken(PMLParser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(PMLParser.CLOSE_PAREN, 0); }
		public VarDeclarationContext(VariableDeclarationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterVarDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitVarDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitVarDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ConstDeclarationContext extends VariableDeclarationStatementContext {
		public TerminalNode CONST() { return getToken(PMLParser.CONST, 0); }
		public List<ConstSpecContext> constSpec() {
			return getRuleContexts(ConstSpecContext.class);
		}
		public ConstSpecContext constSpec(int i) {
			return getRuleContext(ConstSpecContext.class,i);
		}
		public TerminalNode OPEN_PAREN() { return getToken(PMLParser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(PMLParser.CLOSE_PAREN, 0); }
		public ConstDeclarationContext(VariableDeclarationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterConstDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitConstDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitConstDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ShortDeclarationContext extends VariableDeclarationStatementContext {
		public TerminalNode ID() { return getToken(PMLParser.ID, 0); }
		public TerminalNode DECLARE_ASSIGN() { return getToken(PMLParser.DECLARE_ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ShortDeclarationContext(VariableDeclarationStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterShortDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitShortDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitShortDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclarationStatementContext variableDeclarationStatement() throws RecognitionException {
		VariableDeclarationStatementContext _localctx = new VariableDeclarationStatementContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_variableDeclarationStatement);
		int _la;
		try {
			setState(382);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CONST:
				_localctx = new ConstDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(355);
				match(CONST);
				setState(365);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ID:
					{
					setState(356);
					constSpec();
					}
					break;
				case OPEN_PAREN:
					{
					setState(357);
					match(OPEN_PAREN);
					setState(361);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==ID) {
						{
						{
						setState(358);
						constSpec();
						}
						}
						setState(363);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(364);
					match(CLOSE_PAREN);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case VAR:
				_localctx = new VarDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(367);
				match(VAR);
				setState(377);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ID:
					{
					setState(368);
					varSpec();
					}
					break;
				case OPEN_PAREN:
					{
					setState(369);
					match(OPEN_PAREN);
					setState(373);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==ID) {
						{
						{
						setState(370);
						varSpec();
						}
						}
						setState(375);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(376);
					match(CLOSE_PAREN);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case ID:
				_localctx = new ShortDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(379);
				match(ID);
				setState(380);
				match(DECLARE_ASSIGN);
				setState(381);
				expression(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstSpecContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PMLParser.ID, 0); }
		public TerminalNode ASSIGN_EQUALS() { return getToken(PMLParser.ASSIGN_EQUALS, 0); }
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public ConstSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterConstSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitConstSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitConstSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstSpecContext constSpec() throws RecognitionException {
		ConstSpecContext _localctx = new ConstSpecContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_constSpec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(384);
			match(ID);
			setState(385);
			match(ASSIGN_EQUALS);
			setState(386);
			literal();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VarSpecContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PMLParser.ID, 0); }
		public TerminalNode ASSIGN_EQUALS() { return getToken(PMLParser.ASSIGN_EQUALS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public VarSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterVarSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitVarSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitVarSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarSpecContext varSpec() throws RecognitionException {
		VarSpecContext _localctx = new VarSpecContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_varSpec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			match(ID);
			setState(389);
			match(ASSIGN_EQUALS);
			setState(390);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableAssignmentStatementContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PMLParser.ID, 0); }
		public TerminalNode ASSIGN_EQUALS() { return getToken(PMLParser.ASSIGN_EQUALS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(PMLParser.PLUS, 0); }
		public VariableAssignmentStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableAssignmentStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterVariableAssignmentStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitVariableAssignmentStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitVariableAssignmentStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableAssignmentStatementContext variableAssignmentStatement() throws RecognitionException {
		VariableAssignmentStatementContext _localctx = new VariableAssignmentStatementContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_variableAssignmentStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(392);
			match(ID);
			setState(394);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS) {
				{
				setState(393);
				match(PLUS);
				}
			}

			setState(396);
			match(ASSIGN_EQUALS);
			setState(397);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionDefinitionStatementContext extends ParserRuleContext {
		public FunctionSignatureContext functionSignature() {
			return getRuleContext(FunctionSignatureContext.class,0);
		}
		public StatementBlockContext statementBlock() {
			return getRuleContext(StatementBlockContext.class,0);
		}
		public FunctionDefinitionStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionDefinitionStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterFunctionDefinitionStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitFunctionDefinitionStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitFunctionDefinitionStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionDefinitionStatementContext functionDefinitionStatement() throws RecognitionException {
		FunctionDefinitionStatementContext _localctx = new FunctionDefinitionStatementContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_functionDefinitionStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(399);
			functionSignature();
			setState(400);
			statementBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionSignatureContext extends ParserRuleContext {
		public VariableTypeContext returnType;
		public TerminalNode FUNCTION() { return getToken(PMLParser.FUNCTION, 0); }
		public TerminalNode ID() { return getToken(PMLParser.ID, 0); }
		public TerminalNode OPEN_PAREN() { return getToken(PMLParser.OPEN_PAREN, 0); }
		public FormalArgListContext formalArgList() {
			return getRuleContext(FormalArgListContext.class,0);
		}
		public TerminalNode CLOSE_PAREN() { return getToken(PMLParser.CLOSE_PAREN, 0); }
		public VariableTypeContext variableType() {
			return getRuleContext(VariableTypeContext.class,0);
		}
		public FunctionSignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionSignature; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterFunctionSignature(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitFunctionSignature(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitFunctionSignature(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionSignatureContext functionSignature() throws RecognitionException {
		FunctionSignatureContext _localctx = new FunctionSignatureContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_functionSignature);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(402);
			match(FUNCTION);
			setState(403);
			match(ID);
			setState(404);
			match(OPEN_PAREN);
			setState(405);
			formalArgList();
			setState(406);
			match(CLOSE_PAREN);
			setState(408);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 15)) & ~0x3f) == 0 && ((1L << (_la - 15)) & 18027601238949889L) != 0)) {
				{
				setState(407);
				((FunctionSignatureContext)_localctx).returnType = variableType();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FormalArgListContext extends ParserRuleContext {
		public List<FormalArgContext> formalArg() {
			return getRuleContexts(FormalArgContext.class);
		}
		public FormalArgContext formalArg(int i) {
			return getRuleContext(FormalArgContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PMLParser.COMMA, i);
		}
		public FormalArgListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalArgList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterFormalArgList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitFormalArgList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitFormalArgList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormalArgListContext formalArgList() throws RecognitionException {
		FormalArgListContext _localctx = new FormalArgListContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_formalArgList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(418);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 15)) & ~0x3f) == 0 && ((1L << (_la - 15)) & 18027601238949889L) != 0)) {
				{
				setState(410);
				formalArg();
				setState(415);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(411);
					match(COMMA);
					setState(412);
					formalArg();
					}
					}
					setState(417);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FormalArgContext extends ParserRuleContext {
		public VariableTypeContext variableType() {
			return getRuleContext(VariableTypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(PMLParser.ID, 0); }
		public FormalArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalArg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterFormalArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitFormalArg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitFormalArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormalArgContext formalArg() throws RecognitionException {
		FormalArgContext _localctx = new FormalArgContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_formalArg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(420);
			variableType();
			setState(421);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReturnStatementContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(PMLParser.RETURN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitReturnStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitReturnStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReturnStatementContext returnStatement() throws RecognitionException {
		ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_returnStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(423);
			match(RETURN);
			setState(425);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(424);
				expression(0);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionInvokeStatementContext extends ParserRuleContext {
		public FunctionInvokeContext functionInvoke() {
			return getRuleContext(FunctionInvokeContext.class,0);
		}
		public FunctionInvokeStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionInvokeStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterFunctionInvokeStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitFunctionInvokeStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitFunctionInvokeStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionInvokeStatementContext functionInvokeStatement() throws RecognitionException {
		FunctionInvokeStatementContext _localctx = new FunctionInvokeStatementContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_functionInvokeStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(427);
			functionInvoke();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForeachStatementContext extends ParserRuleContext {
		public Token key;
		public Token value;
		public TerminalNode FOREACH() { return getToken(PMLParser.FOREACH, 0); }
		public TerminalNode IN() { return getToken(PMLParser.IN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementBlockContext statementBlock() {
			return getRuleContext(StatementBlockContext.class,0);
		}
		public List<TerminalNode> ID() { return getTokens(PMLParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(PMLParser.ID, i);
		}
		public TerminalNode COMMA() { return getToken(PMLParser.COMMA, 0); }
		public ForeachStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_foreachStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterForeachStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitForeachStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitForeachStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForeachStatementContext foreachStatement() throws RecognitionException {
		ForeachStatementContext _localctx = new ForeachStatementContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_foreachStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(429);
			match(FOREACH);
			setState(430);
			((ForeachStatementContext)_localctx).key = match(ID);
			setState(433);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(431);
				match(COMMA);
				setState(432);
				((ForeachStatementContext)_localctx).value = match(ID);
				}
			}

			setState(435);
			match(IN);
			setState(436);
			expression(0);
			setState(437);
			statementBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BreakStatementContext extends ParserRuleContext {
		public TerminalNode BREAK() { return getToken(PMLParser.BREAK, 0); }
		public BreakStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterBreakStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitBreakStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitBreakStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BreakStatementContext breakStatement() throws RecognitionException {
		BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_breakStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(439);
			match(BREAK);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ContinueStatementContext extends ParserRuleContext {
		public TerminalNode CONTINUE() { return getToken(PMLParser.CONTINUE, 0); }
		public ContinueStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continueStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterContinueStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitContinueStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitContinueStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContinueStatementContext continueStatement() throws RecognitionException {
		ContinueStatementContext _localctx = new ContinueStatementContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_continueStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(441);
			match(CONTINUE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IfStatementContext extends ParserRuleContext {
		public ExpressionContext condition;
		public TerminalNode IF() { return getToken(PMLParser.IF, 0); }
		public StatementBlockContext statementBlock() {
			return getRuleContext(StatementBlockContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<ElseIfStatementContext> elseIfStatement() {
			return getRuleContexts(ElseIfStatementContext.class);
		}
		public ElseIfStatementContext elseIfStatement(int i) {
			return getRuleContext(ElseIfStatementContext.class,i);
		}
		public ElseStatementContext elseStatement() {
			return getRuleContext(ElseStatementContext.class,0);
		}
		public IfStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterIfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitIfStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitIfStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfStatementContext ifStatement() throws RecognitionException {
		IfStatementContext _localctx = new IfStatementContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_ifStatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(443);
			match(IF);
			setState(444);
			((IfStatementContext)_localctx).condition = expression(0);
			setState(445);
			statementBlock();
			setState(449);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(446);
					elseIfStatement();
					}
					} 
				}
				setState(451);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
			}
			setState(453);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(452);
				elseStatement();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ElseIfStatementContext extends ParserRuleContext {
		public ExpressionContext condition;
		public TerminalNode ELSE() { return getToken(PMLParser.ELSE, 0); }
		public TerminalNode IF() { return getToken(PMLParser.IF, 0); }
		public StatementBlockContext statementBlock() {
			return getRuleContext(StatementBlockContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ElseIfStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseIfStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterElseIfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitElseIfStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitElseIfStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElseIfStatementContext elseIfStatement() throws RecognitionException {
		ElseIfStatementContext _localctx = new ElseIfStatementContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_elseIfStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(455);
			match(ELSE);
			setState(456);
			match(IF);
			setState(457);
			((ElseIfStatementContext)_localctx).condition = expression(0);
			setState(458);
			statementBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ElseStatementContext extends ParserRuleContext {
		public TerminalNode ELSE() { return getToken(PMLParser.ELSE, 0); }
		public StatementBlockContext statementBlock() {
			return getRuleContext(StatementBlockContext.class,0);
		}
		public ElseStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterElseStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitElseStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitElseStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElseStatementContext elseStatement() throws RecognitionException {
		ElseStatementContext _localctx = new ElseStatementContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_elseStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(460);
			match(ELSE);
			setState(461);
			statementBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableTypeContext extends ParserRuleContext {
		public VariableTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableType; }
	 
		public VariableTypeContext() { }
		public void copyFrom(VariableTypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MapVarTypeContext extends VariableTypeContext {
		public MapTypeContext mapType() {
			return getRuleContext(MapTypeContext.class,0);
		}
		public MapVarTypeContext(VariableTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterMapVarType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitMapVarType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitMapVarType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringTypeContext extends VariableTypeContext {
		public TerminalNode STRING_TYPE() { return getToken(PMLParser.STRING_TYPE, 0); }
		public StringTypeContext(VariableTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterStringType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitStringType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitStringType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrayVarTypeContext extends VariableTypeContext {
		public ArrayTypeContext arrayType() {
			return getRuleContext(ArrayTypeContext.class,0);
		}
		public ArrayVarTypeContext(VariableTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterArrayVarType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitArrayVarType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitArrayVarType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanTypeContext extends VariableTypeContext {
		public TerminalNode BOOL_TYPE() { return getToken(PMLParser.BOOL_TYPE, 0); }
		public BooleanTypeContext(VariableTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterBooleanType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitBooleanType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitBooleanType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AnyTypeContext extends VariableTypeContext {
		public TerminalNode ANY() { return getToken(PMLParser.ANY, 0); }
		public AnyTypeContext(VariableTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterAnyType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitAnyType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitAnyType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableTypeContext variableType() throws RecognitionException {
		VariableTypeContext _localctx = new VariableTypeContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_variableType);
		try {
			setState(468);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING_TYPE:
				_localctx = new StringTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(463);
				match(STRING_TYPE);
				}
				break;
			case BOOL_TYPE:
				_localctx = new BooleanTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(464);
				match(BOOL_TYPE);
				}
				break;
			case OPEN_BRACKET:
				_localctx = new ArrayVarTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(465);
				arrayType();
				}
				break;
			case MAP:
				_localctx = new MapVarTypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(466);
				mapType();
				}
				break;
			case ANY:
				_localctx = new AnyTypeContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(467);
				match(ANY);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MapTypeContext extends ParserRuleContext {
		public VariableTypeContext keyType;
		public VariableTypeContext valueType;
		public TerminalNode MAP() { return getToken(PMLParser.MAP, 0); }
		public TerminalNode OPEN_BRACKET() { return getToken(PMLParser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(PMLParser.CLOSE_BRACKET, 0); }
		public List<VariableTypeContext> variableType() {
			return getRuleContexts(VariableTypeContext.class);
		}
		public VariableTypeContext variableType(int i) {
			return getRuleContext(VariableTypeContext.class,i);
		}
		public MapTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterMapType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitMapType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitMapType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MapTypeContext mapType() throws RecognitionException {
		MapTypeContext _localctx = new MapTypeContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_mapType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(470);
			match(MAP);
			setState(471);
			match(OPEN_BRACKET);
			setState(472);
			((MapTypeContext)_localctx).keyType = variableType();
			setState(473);
			match(CLOSE_BRACKET);
			setState(474);
			((MapTypeContext)_localctx).valueType = variableType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayTypeContext extends ParserRuleContext {
		public TerminalNode OPEN_BRACKET() { return getToken(PMLParser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(PMLParser.CLOSE_BRACKET, 0); }
		public VariableTypeContext variableType() {
			return getRuleContext(VariableTypeContext.class,0);
		}
		public ArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitArrayType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitArrayType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayTypeContext arrayType() throws RecognitionException {
		ArrayTypeContext _localctx = new ArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_arrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(476);
			match(OPEN_BRACKET);
			setState(477);
			match(CLOSE_BRACKET);
			setState(478);
			variableType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NegateExpressionContext extends ExpressionContext {
		public TerminalNode EXCLAMATION() { return getToken(PMLParser.EXCLAMATION, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public NegateExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterNegateExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitNegateExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitNegateExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LogicalExpressionContext extends ExpressionContext {
		public ExpressionContext left;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode LOGICAL_AND() { return getToken(PMLParser.LOGICAL_AND, 0); }
		public TerminalNode LOGICAL_OR() { return getToken(PMLParser.LOGICAL_OR, 0); }
		public LogicalExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterLogicalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitLogicalExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitLogicalExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PlusExpressionContext extends ExpressionContext {
		public ExpressionContext left;
		public ExpressionContext right;
		public TerminalNode PLUS() { return getToken(PMLParser.PLUS, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public PlusExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterPlusExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitPlusExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitPlusExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunctionInvokeExpressionContext extends ExpressionContext {
		public FunctionInvokeContext functionInvoke() {
			return getRuleContext(FunctionInvokeContext.class,0);
		}
		public FunctionInvokeExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterFunctionInvokeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitFunctionInvokeExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitFunctionInvokeExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class VariableReferenceExpressionContext extends ExpressionContext {
		public VariableReferenceContext variableReference() {
			return getRuleContext(VariableReferenceContext.class,0);
		}
		public VariableReferenceExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterVariableReferenceExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitVariableReferenceExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitVariableReferenceExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LiteralExpressionContext extends ExpressionContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public LiteralExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterLiteralExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitLiteralExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitLiteralExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenExpressionContext extends ExpressionContext {
		public TerminalNode OPEN_PAREN() { return getToken(PMLParser.OPEN_PAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode CLOSE_PAREN() { return getToken(PMLParser.CLOSE_PAREN, 0); }
		public ParenExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterParenExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitParenExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitParenExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PatternExpressionContext extends ExpressionContext {
		public PatternContext pattern() {
			return getRuleContext(PatternContext.class,0);
		}
		public PatternExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterPatternExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitPatternExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitPatternExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EqualsExpressionContext extends ExpressionContext {
		public ExpressionContext left;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode EQUALS() { return getToken(PMLParser.EQUALS, 0); }
		public TerminalNode NOT_EQUALS() { return getToken(PMLParser.NOT_EQUALS, 0); }
		public EqualsExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterEqualsExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitEqualsExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitEqualsExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 100;
		enterRecursionRule(_localctx, 100, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(491);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				{
				_localctx = new VariableReferenceExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(481);
				variableReference(0);
				}
				break;
			case 2:
				{
				_localctx = new FunctionInvokeExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(482);
				functionInvoke();
				}
				break;
			case 3:
				{
				_localctx = new LiteralExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(483);
				literal();
				}
				break;
			case 4:
				{
				_localctx = new NegateExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(484);
				match(EXCLAMATION);
				setState(485);
				expression(6);
				}
				break;
			case 5:
				{
				_localctx = new ParenExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(486);
				match(OPEN_PAREN);
				setState(487);
				expression(0);
				setState(488);
				match(CLOSE_PAREN);
				}
				break;
			case 6:
				{
				_localctx = new PatternExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(490);
				pattern();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(504);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(502);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
					case 1:
						{
						_localctx = new PlusExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((PlusExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(493);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(494);
						match(PLUS);
						setState(495);
						((PlusExpressionContext)_localctx).right = expression(5);
						}
						break;
					case 2:
						{
						_localctx = new EqualsExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((EqualsExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(496);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(497);
						_la = _input.LA(1);
						if ( !(_la==EQUALS || _la==NOT_EQUALS) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(498);
						((EqualsExpressionContext)_localctx).right = expression(4);
						}
						break;
					case 3:
						{
						_localctx = new LogicalExpressionContext(new ExpressionContext(_parentctx, _parentState));
						((LogicalExpressionContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(499);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(500);
						_la = _input.LA(1);
						if ( !(_la==LOGICAL_OR || _la==LOGICAL_AND) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(501);
						((LogicalExpressionContext)_localctx).right = expression(3);
						}
						break;
					}
					} 
				}
				setState(506);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionListContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PMLParser.COMMA, i);
		}
		public ExpressionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterExpressionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitExpressionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitExpressionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionListContext expressionList() throws RecognitionException {
		ExpressionListContext _localctx = new ExpressionListContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_expressionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(507);
			expression(0);
			setState(512);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(508);
				match(COMMA);
				setState(509);
				expression(0);
				}
				}
				setState(514);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PatternContext extends ParserRuleContext {
		public FunctionInvokeContext patternFuncInvoke;
		public TerminalNode ID() { return getToken(PMLParser.ID, 0); }
		public TerminalNode PATTERN_OP() { return getToken(PMLParser.PATTERN_OP, 0); }
		public FunctionInvokeContext functionInvoke() {
			return getRuleContext(FunctionInvokeContext.class,0);
		}
		public TerminalNode OPEN_PAREN() { return getToken(PMLParser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(PMLParser.CLOSE_PAREN, 0); }
		public PatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PatternContext pattern() throws RecognitionException {
		PatternContext _localctx = new PatternContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_pattern);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(516);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_PAREN) {
				{
				setState(515);
				match(OPEN_PAREN);
				}
			}

			setState(518);
			match(ID);
			setState(520);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CLOSE_PAREN) {
				{
				setState(519);
				match(CLOSE_PAREN);
				}
			}

			setState(522);
			match(PATTERN_OP);
			setState(523);
			((PatternContext)_localctx).patternFuncInvoke = functionInvoke();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralContext extends ParserRuleContext {
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	 
		public LiteralContext() { }
		public void copyFrom(LiteralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MapLiteralContext extends LiteralContext {
		public MapLitContext mapLit() {
			return getRuleContext(MapLitContext.class,0);
		}
		public MapLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterMapLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitMapLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitMapLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringLiteralContext extends LiteralContext {
		public StringLitContext stringLit() {
			return getRuleContext(StringLitContext.class,0);
		}
		public StringLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterStringLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitStringLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitStringLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BoolLiteralContext extends LiteralContext {
		public BoolLitContext boolLit() {
			return getRuleContext(BoolLitContext.class,0);
		}
		public BoolLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterBoolLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitBoolLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitBoolLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrayLiteralContext extends LiteralContext {
		public ArrayLitContext arrayLit() {
			return getRuleContext(ArrayLitContext.class,0);
		}
		public ArrayLiteralContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterArrayLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitArrayLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitArrayLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_literal);
		try {
			setState(529);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DOUBLE_QUOTE_STRING:
				_localctx = new StringLiteralContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(525);
				stringLit();
				}
				break;
			case TRUE:
			case FALSE:
				_localctx = new BoolLiteralContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(526);
				boolLit();
				}
				break;
			case OPEN_BRACKET:
				_localctx = new ArrayLiteralContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(527);
				arrayLit();
				}
				break;
			case OPEN_CURLY:
				_localctx = new MapLiteralContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(528);
				mapLit();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StringLitContext extends ParserRuleContext {
		public TerminalNode DOUBLE_QUOTE_STRING() { return getToken(PMLParser.DOUBLE_QUOTE_STRING, 0); }
		public StringLitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringLit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterStringLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitStringLit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitStringLit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringLitContext stringLit() throws RecognitionException {
		StringLitContext _localctx = new StringLitContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_stringLit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(531);
			match(DOUBLE_QUOTE_STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BoolLitContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(PMLParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(PMLParser.FALSE, 0); }
		public BoolLitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolLit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterBoolLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitBoolLit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitBoolLit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolLitContext boolLit() throws RecognitionException {
		BoolLitContext _localctx = new BoolLitContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_boolLit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(533);
			_la = _input.LA(1);
			if ( !(_la==TRUE || _la==FALSE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayLitContext extends ParserRuleContext {
		public TerminalNode OPEN_BRACKET() { return getToken(PMLParser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(PMLParser.CLOSE_BRACKET, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public ArrayLitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayLit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterArrayLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitArrayLit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitArrayLit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayLitContext arrayLit() throws RecognitionException {
		ArrayLitContext _localctx = new ArrayLitContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_arrayLit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(535);
			match(OPEN_BRACKET);
			setState(537);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & 2621615L) != 0)) {
				{
				setState(536);
				expressionList();
				}
			}

			setState(539);
			match(CLOSE_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MapLitContext extends ParserRuleContext {
		public TerminalNode OPEN_CURLY() { return getToken(PMLParser.OPEN_CURLY, 0); }
		public TerminalNode CLOSE_CURLY() { return getToken(PMLParser.CLOSE_CURLY, 0); }
		public List<ElementContext> element() {
			return getRuleContexts(ElementContext.class);
		}
		public ElementContext element(int i) {
			return getRuleContext(ElementContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PMLParser.COMMA, i);
		}
		public MapLitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapLit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterMapLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitMapLit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitMapLit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MapLitContext mapLit() throws RecognitionException {
		MapLitContext _localctx = new MapLitContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_mapLit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(541);
			match(OPEN_CURLY);
			setState(550);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & 2621615L) != 0)) {
				{
				setState(542);
				element();
				setState(547);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(543);
					match(COMMA);
					setState(544);
					element();
					}
					}
					setState(549);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(552);
			match(CLOSE_CURLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ElementContext extends ParserRuleContext {
		public ExpressionContext key;
		public ExpressionContext value;
		public TerminalNode COLON() { return getToken(PMLParser.COLON, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_element; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElementContext element() throws RecognitionException {
		ElementContext _localctx = new ElementContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_element);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(554);
			((ElementContext)_localctx).key = expression(0);
			setState(555);
			match(COLON);
			setState(556);
			((ElementContext)_localctx).value = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableReferenceContext extends ParserRuleContext {
		public VariableReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableReference; }
	 
		public VariableReferenceContext() { }
		public void copyFrom(VariableReferenceContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReferenceByIndexContext extends VariableReferenceContext {
		public VariableReferenceContext variableReference() {
			return getRuleContext(VariableReferenceContext.class,0);
		}
		public IndexContext index() {
			return getRuleContext(IndexContext.class,0);
		}
		public ReferenceByIndexContext(VariableReferenceContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterReferenceByIndex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitReferenceByIndex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitReferenceByIndex(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReferenceByIDContext extends VariableReferenceContext {
		public TerminalNode ID() { return getToken(PMLParser.ID, 0); }
		public ReferenceByIDContext(VariableReferenceContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterReferenceByID(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitReferenceByID(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitReferenceByID(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableReferenceContext variableReference() throws RecognitionException {
		return variableReference(0);
	}

	private VariableReferenceContext variableReference(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		VariableReferenceContext _localctx = new VariableReferenceContext(_ctx, _parentState);
		VariableReferenceContext _prevctx = _localctx;
		int _startState = 118;
		enterRecursionRule(_localctx, 118, RULE_variableReference, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new ReferenceByIDContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(559);
			match(ID);
			}
			_ctx.stop = _input.LT(-1);
			setState(565);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,43,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ReferenceByIndexContext(new VariableReferenceContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_variableReference);
					setState(561);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(562);
					index();
					}
					} 
				}
				setState(567);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,43,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IndexContext extends ParserRuleContext {
		public IndexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_index; }
	 
		public IndexContext() { }
		public void copyFrom(IndexContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DotIndexContext extends IndexContext {
		public IdContext key;
		public TerminalNode DOT() { return getToken(PMLParser.DOT, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public DotIndexContext(IndexContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterDotIndex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitDotIndex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitDotIndex(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BracketIndexContext extends IndexContext {
		public ExpressionContext key;
		public TerminalNode OPEN_BRACKET() { return getToken(PMLParser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(PMLParser.CLOSE_BRACKET, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public BracketIndexContext(IndexContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterBracketIndex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitBracketIndex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitBracketIndex(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IndexContext index() throws RecognitionException {
		IndexContext _localctx = new IndexContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_index);
		try {
			setState(574);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OPEN_BRACKET:
				_localctx = new BracketIndexContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(568);
				match(OPEN_BRACKET);
				setState(569);
				((BracketIndexContext)_localctx).key = expression(0);
				setState(570);
				match(CLOSE_BRACKET);
				}
				break;
			case DOT:
				_localctx = new DotIndexContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(572);
				match(DOT);
				setState(573);
				((DotIndexContext)_localctx).key = id();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PMLParser.ID, 0); }
		public IdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdContext id() throws RecognitionException {
		IdContext _localctx = new IdContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_id);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(576);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionInvokeContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PMLParser.ID, 0); }
		public FunctionInvokeArgsContext functionInvokeArgs() {
			return getRuleContext(FunctionInvokeArgsContext.class,0);
		}
		public FunctionInvokeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionInvoke; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterFunctionInvoke(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitFunctionInvoke(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitFunctionInvoke(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionInvokeContext functionInvoke() throws RecognitionException {
		FunctionInvokeContext _localctx = new FunctionInvokeContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_functionInvoke);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(578);
			match(ID);
			setState(579);
			functionInvokeArgs();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionInvokeArgsContext extends ParserRuleContext {
		public TerminalNode OPEN_PAREN() { return getToken(PMLParser.OPEN_PAREN, 0); }
		public TerminalNode CLOSE_PAREN() { return getToken(PMLParser.CLOSE_PAREN, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public FunctionInvokeArgsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionInvokeArgs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).enterFunctionInvokeArgs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PMLParserListener ) ((PMLParserListener)listener).exitFunctionInvokeArgs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PMLParserVisitor ) return ((PMLParserVisitor<? extends T>)visitor).visitFunctionInvokeArgs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionInvokeArgsContext functionInvokeArgs() throws RecognitionException {
		FunctionInvokeArgsContext _localctx = new FunctionInvokeArgsContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_functionInvokeArgs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(581);
			match(OPEN_PAREN);
			setState(583);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & 2621615L) != 0)) {
				{
				setState(582);
				expressionList();
				}
			}

			setState(585);
			match(CLOSE_PAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 50:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 59:
			return variableReference_sempred((VariableReferenceContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 4);
		case 1:
			return precpred(_ctx, 3);
		case 2:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean variableReference_sempred(VariableReferenceContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001V\u024c\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"+
		"2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"+
		"7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002"+
		"<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0001\u0000\u0005\u0000"+
		"\u0082\b\u0000\n\u0000\f\u0000\u0085\t\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u009e\b\u0001\u0001\u0002\u0001"+
		"\u0002\u0005\u0002\u00a2\b\u0002\n\u0002\f\u0002\u00a5\t\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0003\u0003\u00ae\b\u0003\u0001\u0003\u0003\u0003\u00b1\b\u0003\u0001"+
		"\u0004\u0001\u0004\u0003\u0004\u00b5\b\u0004\u0001\u0004\u0003\u0004\u00b8"+
		"\b\u0004\u0001\u0004\u0003\u0004\u00bb\b\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0005\b\u00ca\b\b"+
		"\n\b\f\b\u00cd\t\b\u0001\b\u0001\b\u0001\t\u0001\t\u0005\t\u00d3\b\t\n"+
		"\t\f\t\u00d6\t\t\u0001\t\u0001\t\u0001\n\u0001\n\u0003\n\u00dc\b\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u00e9\b\f\u0001\f\u0001\f\u0003"+
		"\f\u00ed\b\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0005\u000e\u00f6\b\u000e\n\u000e\f\u000e\u00f9\t\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f"+
		"\u0106\b\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0005\u0010\u010b\b"+
		"\u0010\n\u0010\f\u0010\u010e\t\u0010\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0005\u0012"+
		"\u0118\b\u0012\n\u0012\f\u0012\u011b\t\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u0122\b\u0013\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001"+
		"\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u0159\b\u001c\u0001"+
		"\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001"+
		"\u001f\u0005\u001f\u0168\b\u001f\n\u001f\f\u001f\u016b\t\u001f\u0001\u001f"+
		"\u0003\u001f\u016e\b\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f"+
		"\u0005\u001f\u0174\b\u001f\n\u001f\f\u001f\u0177\t\u001f\u0001\u001f\u0003"+
		"\u001f\u017a\b\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u017f"+
		"\b\u001f\u0001 \u0001 \u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0001"+
		"\"\u0001\"\u0003\"\u018b\b\"\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001"+
		"#\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0003$\u0199\b$\u0001%\u0001"+
		"%\u0001%\u0005%\u019e\b%\n%\f%\u01a1\t%\u0003%\u01a3\b%\u0001&\u0001&"+
		"\u0001&\u0001\'\u0001\'\u0003\'\u01aa\b\'\u0001(\u0001(\u0001)\u0001)"+
		"\u0001)\u0001)\u0003)\u01b2\b)\u0001)\u0001)\u0001)\u0001)\u0001*\u0001"+
		"*\u0001+\u0001+\u0001,\u0001,\u0001,\u0001,\u0005,\u01c0\b,\n,\f,\u01c3"+
		"\t,\u0001,\u0003,\u01c6\b,\u0001-\u0001-\u0001-\u0001-\u0001-\u0001.\u0001"+
		".\u0001.\u0001/\u0001/\u0001/\u0001/\u0001/\u0003/\u01d5\b/\u00010\u0001"+
		"0\u00010\u00010\u00010\u00010\u00011\u00011\u00011\u00011\u00012\u0001"+
		"2\u00012\u00012\u00012\u00012\u00012\u00012\u00012\u00012\u00012\u0003"+
		"2\u01ec\b2\u00012\u00012\u00012\u00012\u00012\u00012\u00012\u00012\u0001"+
		"2\u00052\u01f7\b2\n2\f2\u01fa\t2\u00013\u00013\u00013\u00053\u01ff\b3"+
		"\n3\f3\u0202\t3\u00014\u00034\u0205\b4\u00014\u00014\u00034\u0209\b4\u0001"+
		"4\u00014\u00014\u00015\u00015\u00015\u00015\u00035\u0212\b5\u00016\u0001"+
		"6\u00017\u00017\u00018\u00018\u00038\u021a\b8\u00018\u00018\u00019\u0001"+
		"9\u00019\u00019\u00059\u0222\b9\n9\f9\u0225\t9\u00039\u0227\b9\u00019"+
		"\u00019\u0001:\u0001:\u0001:\u0001:\u0001;\u0001;\u0001;\u0001;\u0001"+
		";\u0005;\u0234\b;\n;\f;\u0237\t;\u0001<\u0001<\u0001<\u0001<\u0001<\u0001"+
		"<\u0003<\u023f\b<\u0001=\u0001=\u0001>\u0001>\u0001>\u0001?\u0001?\u0003"+
		"?\u0248\b?\u0001?\u0001?\u0001?\u0000\u0002dv@\u0000\u0002\u0004\u0006"+
		"\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,."+
		"02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0000\u0007\u0002\u0000#$\'"+
		"(\u0003\u0000\u0010\u0010$$((\u0001\u0000\r\u000e\u0002\u0000\"$\'(\u0001"+
		"\u0000OP\u0001\u0000MN\u0001\u0000>?\u025b\u0000\u0083\u0001\u0000\u0000"+
		"\u0000\u0002\u009d\u0001\u0000\u0000\u0000\u0004\u009f\u0001\u0000\u0000"+
		"\u0000\u0006\u00a8\u0001\u0000\u0000\u0000\b\u00b2\u0001\u0000\u0000\u0000"+
		"\n\u00be\u0001\u0000\u0000\u0000\f\u00c1\u0001\u0000\u0000\u0000\u000e"+
		"\u00c4\u0001\u0000\u0000\u0000\u0010\u00c7\u0001\u0000\u0000\u0000\u0012"+
		"\u00d0\u0001\u0000\u0000\u0000\u0014\u00d9\u0001\u0000\u0000\u0000\u0016"+
		"\u00dd\u0001\u0000\u0000\u0000\u0018\u00e3\u0001\u0000\u0000\u0000\u001a"+
		"\u00ee\u0001\u0000\u0000\u0000\u001c\u00f0\u0001\u0000\u0000\u0000\u001e"+
		"\u00fc\u0001\u0000\u0000\u0000 \u010c\u0001\u0000\u0000\u0000\"\u010f"+
		"\u0001\u0000\u0000\u0000$\u0115\u0001\u0000\u0000\u0000&\u0121\u0001\u0000"+
		"\u0000\u0000(\u0123\u0001\u0000\u0000\u0000*\u0130\u0001\u0000\u0000\u0000"+
		",\u0136\u0001\u0000\u0000\u0000.\u013b\u0001\u0000\u0000\u00000\u0140"+
		"\u0001\u0000\u0000\u00002\u0147\u0001\u0000\u0000\u00004\u014c\u0001\u0000"+
		"\u0000\u00006\u014f\u0001\u0000\u0000\u00008\u0158\u0001\u0000\u0000\u0000"+
		":\u015a\u0001\u0000\u0000\u0000<\u015c\u0001\u0000\u0000\u0000>\u017e"+
		"\u0001\u0000\u0000\u0000@\u0180\u0001\u0000\u0000\u0000B\u0184\u0001\u0000"+
		"\u0000\u0000D\u0188\u0001\u0000\u0000\u0000F\u018f\u0001\u0000\u0000\u0000"+
		"H\u0192\u0001\u0000\u0000\u0000J\u01a2\u0001\u0000\u0000\u0000L\u01a4"+
		"\u0001\u0000\u0000\u0000N\u01a7\u0001\u0000\u0000\u0000P\u01ab\u0001\u0000"+
		"\u0000\u0000R\u01ad\u0001\u0000\u0000\u0000T\u01b7\u0001\u0000\u0000\u0000"+
		"V\u01b9\u0001\u0000\u0000\u0000X\u01bb\u0001\u0000\u0000\u0000Z\u01c7"+
		"\u0001\u0000\u0000\u0000\\\u01cc\u0001\u0000\u0000\u0000^\u01d4\u0001"+
		"\u0000\u0000\u0000`\u01d6\u0001\u0000\u0000\u0000b\u01dc\u0001\u0000\u0000"+
		"\u0000d\u01eb\u0001\u0000\u0000\u0000f\u01fb\u0001\u0000\u0000\u0000h"+
		"\u0204\u0001\u0000\u0000\u0000j\u0211\u0001\u0000\u0000\u0000l\u0213\u0001"+
		"\u0000\u0000\u0000n\u0215\u0001\u0000\u0000\u0000p\u0217\u0001\u0000\u0000"+
		"\u0000r\u021d\u0001\u0000\u0000\u0000t\u022a\u0001\u0000\u0000\u0000v"+
		"\u022e\u0001\u0000\u0000\u0000x\u023e\u0001\u0000\u0000\u0000z\u0240\u0001"+
		"\u0000\u0000\u0000|\u0242\u0001\u0000\u0000\u0000~\u0245\u0001\u0000\u0000"+
		"\u0000\u0080\u0082\u0003\u0002\u0001\u0000\u0081\u0080\u0001\u0000\u0000"+
		"\u0000\u0082\u0085\u0001\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000"+
		"\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0086\u0001\u0000\u0000"+
		"\u0000\u0085\u0083\u0001\u0000\u0000\u0000\u0086\u0087\u0005\u0000\u0000"+
		"\u0001\u0087\u0001\u0001\u0000\u0000\u0000\u0088\u009e\u0003\u0006\u0003"+
		"\u0000\u0089\u009e\u0003\u0018\f\u0000\u008a\u009e\u0003\u001c\u000e\u0000"+
		"\u008b\u009e\u0003(\u0014\u0000\u008c\u009e\u0003*\u0015\u0000\u008d\u009e"+
		"\u0003,\u0016\u0000\u008e\u009e\u0003.\u0017\u0000\u008f\u009e\u00030"+
		"\u0018\u0000\u0090\u009e\u00032\u0019\u0000\u0091\u009e\u00034\u001a\u0000"+
		"\u0092\u009e\u00036\u001b\u0000\u0093\u009e\u0003<\u001e\u0000\u0094\u009e"+
		"\u0003D\"\u0000\u0095\u009e\u0003>\u001f\u0000\u0096\u009e\u0003F#\u0000"+
		"\u0097\u009e\u0003N\'\u0000\u0098\u009e\u0003R)\u0000\u0099\u009e\u0003"+
		"T*\u0000\u009a\u009e\u0003V+\u0000\u009b\u009e\u0003P(\u0000\u009c\u009e"+
		"\u0003X,\u0000\u009d\u0088\u0001\u0000\u0000\u0000\u009d\u0089\u0001\u0000"+
		"\u0000\u0000\u009d\u008a\u0001\u0000\u0000\u0000\u009d\u008b\u0001\u0000"+
		"\u0000\u0000\u009d\u008c\u0001\u0000\u0000\u0000\u009d\u008d\u0001\u0000"+
		"\u0000\u0000\u009d\u008e\u0001\u0000\u0000\u0000\u009d\u008f\u0001\u0000"+
		"\u0000\u0000\u009d\u0090\u0001\u0000\u0000\u0000\u009d\u0091\u0001\u0000"+
		"\u0000\u0000\u009d\u0092\u0001\u0000\u0000\u0000\u009d\u0093\u0001\u0000"+
		"\u0000\u0000\u009d\u0094\u0001\u0000\u0000\u0000\u009d\u0095\u0001\u0000"+
		"\u0000\u0000\u009d\u0096\u0001\u0000\u0000\u0000\u009d\u0097\u0001\u0000"+
		"\u0000\u0000\u009d\u0098\u0001\u0000\u0000\u0000\u009d\u0099\u0001\u0000"+
		"\u0000\u0000\u009d\u009a\u0001\u0000\u0000\u0000\u009d\u009b\u0001\u0000"+
		"\u0000\u0000\u009d\u009c\u0001\u0000\u0000\u0000\u009e\u0003\u0001\u0000"+
		"\u0000\u0000\u009f\u00a3\u0005C\u0000\u0000\u00a0\u00a2\u0003\u0002\u0001"+
		"\u0000\u00a1\u00a0\u0001\u0000\u0000\u0000\u00a2\u00a5\u0001\u0000\u0000"+
		"\u0000\u00a3\u00a1\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000"+
		"\u0000\u00a4\u00a6\u0001\u0000\u0000\u0000\u00a5\u00a3\u0001\u0000\u0000"+
		"\u0000\u00a6\u00a7\u0005D\u0000\u0000\u00a7\u0005\u0001\u0000\u0000\u0000"+
		"\u00a8\u00a9\u0005\u0001\u0000\u0000\u00a9\u00aa\u0005\"\u0000\u0000\u00aa"+
		"\u00ad\u0003d2\u0000\u00ab\u00ac\u0005\u0017\u0000\u0000\u00ac\u00ae\u0003"+
		"d2\u0000\u00ad\u00ab\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000"+
		"\u0000\u00ae\u00b0\u0001\u0000\u0000\u0000\u00af\u00b1\u0003\b\u0004\u0000"+
		"\u00b0\u00af\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000"+
		"\u00b1\u0007\u0001\u0000\u0000\u0000\u00b2\u00b4\u0005C\u0000\u0000\u00b3"+
		"\u00b5\u0003\n\u0005\u0000\u00b4\u00b3\u0001\u0000\u0000\u0000\u00b4\u00b5"+
		"\u0001\u0000\u0000\u0000\u00b5\u00b7\u0001\u0000\u0000\u0000\u00b6\u00b8"+
		"\u0003\f\u0006\u0000\u00b7\u00b6\u0001\u0000\u0000\u0000\u00b7\u00b8\u0001"+
		"\u0000\u0000\u0000\u00b8\u00ba\u0001\u0000\u0000\u0000\u00b9\u00bb\u0003"+
		"\u000e\u0007\u0000\u00ba\u00b9\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001"+
		"\u0000\u0000\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc\u00bd\u0005"+
		"D\u0000\u0000\u00bd\t\u0001\u0000\u0000\u0000\u00be\u00bf\u0005%\u0000"+
		"\u0000\u00bf\u00c0\u0003\u0010\b\u0000\u00c0\u000b\u0001\u0000\u0000\u0000"+
		"\u00c1\u00c2\u0005&\u0000\u0000\u00c2\u00c3\u0003\u0010\b\u0000\u00c3"+
		"\r\u0001\u0000\u0000\u0000\u00c4\u00c5\u0005+\u0000\u0000\u00c5\u00c6"+
		"\u0003\u0012\t\u0000\u00c6\u000f\u0001\u0000\u0000\u0000\u00c7\u00cb\u0005"+
		"C\u0000\u0000\u00c8\u00ca\u0003\u0014\n\u0000\u00c9\u00c8\u0001\u0000"+
		"\u0000\u0000\u00ca\u00cd\u0001\u0000\u0000\u0000\u00cb\u00c9\u0001\u0000"+
		"\u0000\u0000\u00cb\u00cc\u0001\u0000\u0000\u0000\u00cc\u00ce\u0001\u0000"+
		"\u0000\u0000\u00cd\u00cb\u0001\u0000\u0000\u0000\u00ce\u00cf\u0005D\u0000"+
		"\u0000\u00cf\u0011\u0001\u0000\u0000\u0000\u00d0\u00d4\u0005C\u0000\u0000"+
		"\u00d1\u00d3\u0003\u0016\u000b\u0000\u00d2\u00d1\u0001\u0000\u0000\u0000"+
		"\u00d3\u00d6\u0001\u0000\u0000\u0000\u00d4\u00d2\u0001\u0000\u0000\u0000"+
		"\u00d4\u00d5\u0001\u0000\u0000\u0000\u00d5\u00d7\u0001\u0000\u0000\u0000"+
		"\u00d6\u00d4\u0001\u0000\u0000\u0000\u00d7\u00d8\u0005D\u0000\u0000\u00d8"+
		"\u0013\u0001\u0000\u0000\u0000\u00d9\u00db\u0003d2\u0000\u00da\u00dc\u0003"+
		"d2\u0000\u00db\u00da\u0001\u0000\u0000\u0000\u00db\u00dc\u0001\u0000\u0000"+
		"\u0000\u00dc\u0015\u0001\u0000\u0000\u0000\u00dd\u00de\u0003d2\u0000\u00de"+
		"\u00df\u0005\u001b\u0000\u0000\u00df\u00e0\u0003d2\u0000\u00e0\u00e1\u0005"+
		"\u001c\u0000\u0000\u00e1\u00e2\u0003d2\u0000\u00e2\u0017\u0001\u0000\u0000"+
		"\u0000\u00e3\u00e4\u0005\u0001\u0000\u0000\u00e4\u00e5\u0003\u001a\r\u0000"+
		"\u00e5\u00e8\u0003d2\u0000\u00e6\u00e7\u0005\u0017\u0000\u0000\u00e7\u00e9"+
		"\u0003d2\u0000\u00e8\u00e6\u0001\u0000\u0000\u0000\u00e8\u00e9\u0001\u0000"+
		"\u0000\u0000\u00e9\u00ec\u0001\u0000\u0000\u0000\u00ea\u00eb\u0005\u0013"+
		"\u0000\u0000\u00eb\u00ed\u0003d2\u0000\u00ec\u00ea\u0001\u0000\u0000\u0000"+
		"\u00ec\u00ed\u0001\u0000\u0000\u0000\u00ed\u0019\u0001\u0000\u0000\u0000"+
		"\u00ee\u00ef\u0007\u0000\u0000\u0000\u00ef\u001b\u0001\u0000\u0000\u0000"+
		"\u00f0\u00f1\u0005\u0001\u0000\u0000\u00f1\u00f2\u0005 \u0000\u0000\u00f2"+
		"\u00f3\u0003d2\u0000\u00f3\u00f7\u0005C\u0000\u0000\u00f4\u00f6\u0003"+
		"\u001e\u000f\u0000\u00f5\u00f4\u0001\u0000\u0000\u0000\u00f6\u00f9\u0001"+
		"\u0000\u0000\u0000\u00f7\u00f5\u0001\u0000\u0000\u0000\u00f7\u00f8\u0001"+
		"\u0000\u0000\u0000\u00f8\u00fa\u0001\u0000\u0000\u0000\u00f9\u00f7\u0001"+
		"\u0000\u0000\u0000\u00fa\u00fb\u0005D\u0000\u0000\u00fb\u001d\u0001\u0000"+
		"\u0000\u0000\u00fc\u00fd\u0005\u0001\u0000\u0000\u00fd\u00fe\u0005\u0005"+
		"\u0000\u0000\u00fe\u00ff\u0003d2\u0000\u00ff\u0100\u0005\u0006\u0000\u0000"+
		"\u0100\u0101\u0003h4\u0000\u0101\u0102\u0005\u0007\u0000\u0000\u0102\u0105"+
		"\u0003h4\u0000\u0103\u0104\u0005\t\u0000\u0000\u0104\u0106\u0003 \u0010"+
		"\u0000\u0105\u0103\u0001\u0000\u0000\u0000\u0105\u0106\u0001\u0000\u0000"+
		"\u0000\u0106\u0107\u0001\u0000\u0000\u0000\u0107\u0108\u0003\"\u0011\u0000"+
		"\u0108\u001f\u0001\u0000\u0000\u0000\u0109\u010b\u0003h4\u0000\u010a\u0109"+
		"\u0001\u0000\u0000\u0000\u010b\u010e\u0001\u0000\u0000\u0000\u010c\u010a"+
		"\u0001\u0000\u0000\u0000\u010c\u010d\u0001\u0000\u0000\u0000\u010d!\u0001"+
		"\u0000\u0000\u0000\u010e\u010c\u0001\u0000\u0000\u0000\u010f\u0110\u0005"+
		"\u000b\u0000\u0000\u0110\u0111\u0005A\u0000\u0000\u0111\u0112\u0005@\u0000"+
		"\u0000\u0112\u0113\u0005B\u0000\u0000\u0113\u0114\u0003$\u0012\u0000\u0114"+
		"#\u0001\u0000\u0000\u0000\u0115\u0119\u0005C\u0000\u0000\u0116\u0118\u0003"+
		"&\u0013\u0000\u0117\u0116\u0001\u0000\u0000\u0000\u0118\u011b\u0001\u0000"+
		"\u0000\u0000\u0119\u0117\u0001\u0000\u0000\u0000\u0119\u011a\u0001\u0000"+
		"\u0000\u0000\u011a\u011c\u0001\u0000\u0000\u0000\u011b\u0119\u0001\u0000"+
		"\u0000\u0000\u011c\u011d\u0005D\u0000\u0000\u011d%\u0001\u0000\u0000\u0000"+
		"\u011e\u0122\u0003\u0002\u0001\u0000\u011f\u0122\u0003\u001e\u000f\u0000"+
		"\u0120\u0122\u0003<\u001e\u0000\u0121\u011e\u0001\u0000\u0000\u0000\u0121"+
		"\u011f\u0001\u0000\u0000\u0000\u0121\u0120\u0001\u0000\u0000\u0000\u0122"+
		"\'\u0001\u0000\u0000\u0000\u0123\u0124\u0005\u0001\u0000\u0000\u0124\u0125"+
		"\u0005\u001f\u0000\u0000\u0125\u0126\u0003d2\u0000\u0126\u0127\u0005\u001e"+
		"\u0000\u0000\u0127\u0128\u0007\u0001\u0000\u0000\u0128\u0129\u0003d2\u0000"+
		"\u0129\u012a\u0005!\u0000\u0000\u012a\u012b\u0003d2\u0000\u012b\u012c"+
		"\u0005\t\u0000\u0000\u012c\u012d\u0007\u0002\u0000\u0000\u012d\u012e\u0005"+
		"\u0018\u0000\u0000\u012e\u012f\u0003d2\u0000\u012f)\u0001\u0000\u0000"+
		"\u0000\u0130\u0131\u0005\u0016\u0000\u0000\u0131\u0132\u0005\u0018\u0000"+
		"\u0000\u0132\u0133\u0003d2\u0000\u0133\u0134\u0005\u0019\u0000\u0000\u0134"+
		"\u0135\u0003d2\u0000\u0135+\u0001\u0000\u0000\u0000\u0136\u0137\u0005"+
		"\u0012\u0000\u0000\u0137\u0138\u0003d2\u0000\u0138\u0139\u0005\u0019\u0000"+
		"\u0000\u0139\u013a\u0003d2\u0000\u013a-\u0001\u0000\u0000\u0000\u013b"+
		"\u013c\u0005\u0014\u0000\u0000\u013c\u013d\u0003d2\u0000\u013d\u013e\u0005"+
		"\u0015\u0000\u0000\u013e\u013f\u0003d2\u0000\u013f/\u0001\u0000\u0000"+
		"\u0000\u0140\u0141\u0005\u001a\u0000\u0000\u0141\u0142\u0003d2\u0000\u0142"+
		"\u0143\u0005\u001b\u0000\u0000\u0143\u0144\u0003d2\u0000\u0144\u0145\u0005"+
		"\u001c\u0000\u0000\u0145\u0146\u0003d2\u0000\u01461\u0001\u0000\u0000"+
		"\u0000\u0147\u0148\u0005\u001d\u0000\u0000\u0148\u0149\u0003d2\u0000\u0149"+
		"\u014a\u0005\u001b\u0000\u0000\u014a\u014b\u0003d2\u0000\u014b3\u0001"+
		"\u0000\u0000\u0000\u014c\u014d\u0005\u0011\u0000\u0000\u014d\u014e\u0003"+
		"d2\u0000\u014e5\u0001\u0000\u0000\u0000\u014f\u0150\u0005\u0002\u0000"+
		"\u0000\u0150\u0151\u00038\u001c\u0000\u0151\u0152\u0003d2\u0000\u0152"+
		"7\u0001\u0000\u0000\u0000\u0153\u0159\u0003:\u001d\u0000\u0154\u0159\u0005"+
		" \u0000\u0000\u0155\u0159\u0005\u001f\u0000\u0000\u0156\u0159\u0005.\u0000"+
		"\u0000\u0157\u0159\u00052\u0000\u0000\u0158\u0153\u0001\u0000\u0000\u0000"+
		"\u0158\u0154\u0001\u0000\u0000\u0000\u0158\u0155\u0001\u0000\u0000\u0000"+
		"\u0158\u0156\u0001\u0000\u0000\u0000\u0158\u0157\u0001\u0000\u0000\u0000"+
		"\u01599\u0001\u0000\u0000\u0000\u015a\u015b\u0007\u0003\u0000\u0000\u015b"+
		";\u0001\u0000\u0000\u0000\u015c\u015d\u0005\u0002\u0000\u0000\u015d\u015e"+
		"\u0005\u0005\u0000\u0000\u015e\u015f\u0003d2\u0000\u015f\u0160\u0005\u0015"+
		"\u0000\u0000\u0160\u0161\u0005 \u0000\u0000\u0161\u0162\u0003d2\u0000"+
		"\u0162=\u0001\u0000\u0000\u0000\u0163\u016d\u00052\u0000\u0000\u0164\u016e"+
		"\u0003@ \u0000\u0165\u0169\u0005A\u0000\u0000\u0166\u0168\u0003@ \u0000"+
		"\u0167\u0166\u0001\u0000\u0000\u0000\u0168\u016b\u0001\u0000\u0000\u0000"+
		"\u0169\u0167\u0001\u0000\u0000\u0000\u0169\u016a\u0001\u0000\u0000\u0000"+
		"\u016a\u016c\u0001\u0000\u0000\u0000\u016b\u0169\u0001\u0000\u0000\u0000"+
		"\u016c\u016e\u0005B\u0000\u0000\u016d\u0164\u0001\u0000\u0000\u0000\u016d"+
		"\u0165\u0001\u0000\u0000\u0000\u016e\u017f\u0001\u0000\u0000\u0000\u016f"+
		"\u0179\u00058\u0000\u0000\u0170\u017a\u0003B!\u0000\u0171\u0175\u0005"+
		"A\u0000\u0000\u0172\u0174\u0003B!\u0000\u0173\u0172\u0001\u0000\u0000"+
		"\u0000\u0174\u0177\u0001\u0000\u0000\u0000\u0175\u0173\u0001\u0000\u0000"+
		"\u0000\u0175\u0176\u0001\u0000\u0000\u0000\u0176\u0178\u0001\u0000\u0000"+
		"\u0000\u0177\u0175\u0001\u0000\u0000\u0000\u0178\u017a\u0005B\u0000\u0000"+
		"\u0179\u0170\u0001\u0000\u0000\u0000\u0179\u0171\u0001\u0000\u0000\u0000"+
		"\u017a\u017f\u0001\u0000\u0000\u0000\u017b\u017c\u0005@\u0000\u0000\u017c"+
		"\u017d\u0005L\u0000\u0000\u017d\u017f\u0003d2\u0000\u017e\u0163\u0001"+
		"\u0000\u0000\u0000\u017e\u016f\u0001\u0000\u0000\u0000\u017e\u017b\u0001"+
		"\u0000\u0000\u0000\u017f?\u0001\u0000\u0000\u0000\u0180\u0181\u0005@\u0000"+
		"\u0000\u0181\u0182\u0005G\u0000\u0000\u0182\u0183\u0003j5\u0000\u0183"+
		"A\u0001\u0000\u0000\u0000\u0184\u0185\u0005@\u0000\u0000\u0185\u0186\u0005"+
		"G\u0000\u0000\u0186\u0187\u0003d2\u0000\u0187C\u0001\u0000\u0000\u0000"+
		"\u0188\u018a\u0005@\u0000\u0000\u0189\u018b\u0005R\u0000\u0000\u018a\u0189"+
		"\u0001\u0000\u0000\u0000\u018a\u018b\u0001\u0000\u0000\u0000\u018b\u018c"+
		"\u0001\u0000\u0000\u0000\u018c\u018d\u0005G\u0000\u0000\u018d\u018e\u0003"+
		"d2\u0000\u018eE\u0001\u0000\u0000\u0000\u018f\u0190\u0003H$\u0000\u0190"+
		"\u0191\u0003\u0004\u0002\u0000\u0191G\u0001\u0000\u0000\u0000\u0192\u0193"+
		"\u0005.\u0000\u0000\u0193\u0194\u0005@\u0000\u0000\u0194\u0195\u0005A"+
		"\u0000\u0000\u0195\u0196\u0003J%\u0000\u0196\u0198\u0005B\u0000\u0000"+
		"\u0197\u0199\u0003^/\u0000\u0198\u0197\u0001\u0000\u0000\u0000\u0198\u0199"+
		"\u0001\u0000\u0000\u0000\u0199I\u0001\u0000\u0000\u0000\u019a\u019f\u0003"+
		"L&\u0000\u019b\u019c\u0005H\u0000\u0000\u019c\u019e\u0003L&\u0000\u019d"+
		"\u019b\u0001\u0000\u0000\u0000\u019e\u01a1\u0001\u0000\u0000\u0000\u019f"+
		"\u019d\u0001\u0000\u0000\u0000\u019f\u01a0\u0001\u0000\u0000\u0000\u01a0"+
		"\u01a3\u0001\u0000\u0000\u0000\u01a1\u019f\u0001\u0000\u0000\u0000\u01a2"+
		"\u019a\u0001\u0000\u0000\u0000\u01a2\u01a3\u0001\u0000\u0000\u0000\u01a3"+
		"K\u0001\u0000\u0000\u0000\u01a4\u01a5\u0003^/\u0000\u01a5\u01a6\u0005"+
		"@\u0000\u0000\u01a6M\u0001\u0000\u0000\u0000\u01a7\u01a9\u00057\u0000"+
		"\u0000\u01a8\u01aa\u0003d2\u0000\u01a9\u01a8\u0001\u0000\u0000\u0000\u01a9"+
		"\u01aa\u0001\u0000\u0000\u0000\u01aaO\u0001\u0000\u0000\u0000\u01ab\u01ac"+
		"\u0003|>\u0000\u01acQ\u0001\u0000\u0000\u0000\u01ad\u01ae\u00056\u0000"+
		"\u0000\u01ae\u01b1\u0005@\u0000\u0000\u01af\u01b0\u0005H\u0000\u0000\u01b0"+
		"\u01b2\u0005@\u0000\u0000\u01b1\u01af\u0001\u0000\u0000\u0000\u01b1\u01b2"+
		"\u0001\u0000\u0000\u0000\u01b2\u01b3\u0001\u0000\u0000\u0000\u01b3\u01b4"+
		"\u0005\n\u0000\u0000\u01b4\u01b5\u0003d2\u0000\u01b5\u01b6\u0003\u0004"+
		"\u0002\u0000\u01b6S\u0001\u0000\u0000\u0000\u01b7\u01b8\u0005,\u0000\u0000"+
		"\u01b8U\u0001\u0000\u0000\u0000\u01b9\u01ba\u00055\u0000\u0000\u01baW"+
		"\u0001\u0000\u0000\u0000\u01bb\u01bc\u00053\u0000\u0000\u01bc\u01bd\u0003"+
		"d2\u0000\u01bd\u01c1\u0003\u0004\u0002\u0000\u01be\u01c0\u0003Z-\u0000"+
		"\u01bf\u01be\u0001\u0000\u0000\u0000\u01c0\u01c3\u0001\u0000\u0000\u0000"+
		"\u01c1\u01bf\u0001\u0000\u0000\u0000\u01c1\u01c2\u0001\u0000\u0000\u0000"+
		"\u01c2\u01c5\u0001\u0000\u0000\u0000\u01c3\u01c1\u0001\u0000\u0000\u0000"+
		"\u01c4\u01c6\u0003\\.\u0000\u01c5\u01c4\u0001\u0000\u0000\u0000\u01c5"+
		"\u01c6\u0001\u0000\u0000\u0000\u01c6Y\u0001\u0000\u0000\u0000\u01c7\u01c8"+
		"\u00051\u0000\u0000\u01c8\u01c9\u00053\u0000\u0000\u01c9\u01ca\u0003d"+
		"2\u0000\u01ca\u01cb\u0003\u0004\u0002\u0000\u01cb[\u0001\u0000\u0000\u0000"+
		"\u01cc\u01cd\u00051\u0000\u0000\u01cd\u01ce\u0003\u0004\u0002\u0000\u01ce"+
		"]\u0001\u0000\u0000\u0000\u01cf\u01d5\u00059\u0000\u0000\u01d0\u01d5\u0005"+
		":\u0000\u0000\u01d1\u01d5\u0003b1\u0000\u01d2\u01d5\u0003`0\u0000\u01d3"+
		"\u01d5\u0005\u000f\u0000\u0000\u01d4\u01cf\u0001\u0000\u0000\u0000\u01d4"+
		"\u01d0\u0001\u0000\u0000\u0000\u01d4\u01d1\u0001\u0000\u0000\u0000\u01d4"+
		"\u01d2\u0001\u0000\u0000\u0000\u01d4\u01d3\u0001\u0000\u0000\u0000\u01d5"+
		"_\u0001\u0000\u0000\u0000\u01d6\u01d7\u00050\u0000\u0000\u01d7\u01d8\u0005"+
		"E\u0000\u0000\u01d8\u01d9\u0003^/\u0000\u01d9\u01da\u0005F\u0000\u0000"+
		"\u01da\u01db\u0003^/\u0000\u01dba\u0001\u0000\u0000\u0000\u01dc\u01dd"+
		"\u0005E\u0000\u0000\u01dd\u01de\u0005F\u0000\u0000\u01de\u01df\u0003^"+
		"/\u0000\u01dfc\u0001\u0000\u0000\u0000\u01e0\u01e1\u00062\uffff\uffff"+
		"\u0000\u01e1\u01ec\u0003v;\u0000\u01e2\u01ec\u0003|>\u0000\u01e3\u01ec"+
		"\u0003j5\u0000\u01e4\u01e5\u0005Q\u0000\u0000\u01e5\u01ec\u0003d2\u0006"+
		"\u01e6\u01e7\u0005A\u0000\u0000\u01e7\u01e8\u0003d2\u0000\u01e8\u01e9"+
		"\u0005B\u0000\u0000\u01e9\u01ec\u0001\u0000\u0000\u0000\u01ea\u01ec\u0003"+
		"h4\u0000\u01eb\u01e0\u0001\u0000\u0000\u0000\u01eb\u01e2\u0001\u0000\u0000"+
		"\u0000\u01eb\u01e3\u0001\u0000\u0000\u0000\u01eb\u01e4\u0001\u0000\u0000"+
		"\u0000\u01eb\u01e6\u0001\u0000\u0000\u0000\u01eb\u01ea\u0001\u0000\u0000"+
		"\u0000\u01ec\u01f8\u0001\u0000\u0000\u0000\u01ed\u01ee\n\u0004\u0000\u0000"+
		"\u01ee\u01ef\u0005R\u0000\u0000\u01ef\u01f7\u0003d2\u0005\u01f0\u01f1"+
		"\n\u0003\u0000\u0000\u01f1\u01f2\u0007\u0004\u0000\u0000\u01f2\u01f7\u0003"+
		"d2\u0004\u01f3\u01f4\n\u0002\u0000\u0000\u01f4\u01f5\u0007\u0005\u0000"+
		"\u0000\u01f5\u01f7\u0003d2\u0003\u01f6\u01ed\u0001\u0000\u0000\u0000\u01f6"+
		"\u01f0\u0001\u0000\u0000\u0000\u01f6\u01f3\u0001\u0000\u0000\u0000\u01f7"+
		"\u01fa\u0001\u0000\u0000\u0000\u01f8\u01f6\u0001\u0000\u0000\u0000\u01f8"+
		"\u01f9\u0001\u0000\u0000\u0000\u01f9e\u0001\u0000\u0000\u0000\u01fa\u01f8"+
		"\u0001\u0000\u0000\u0000\u01fb\u0200\u0003d2\u0000\u01fc\u01fd\u0005H"+
		"\u0000\u0000\u01fd\u01ff\u0003d2\u0000\u01fe\u01fc\u0001\u0000\u0000\u0000"+
		"\u01ff\u0202\u0001\u0000\u0000\u0000\u0200\u01fe\u0001\u0000\u0000\u0000"+
		"\u0200\u0201\u0001\u0000\u0000\u0000\u0201g\u0001\u0000\u0000\u0000\u0202"+
		"\u0200\u0001\u0000\u0000\u0000\u0203\u0205\u0005A\u0000\u0000\u0204\u0203"+
		"\u0001\u0000\u0000\u0000\u0204\u0205\u0001\u0000\u0000\u0000\u0205\u0206"+
		"\u0001\u0000\u0000\u0000\u0206\u0208\u0005@\u0000\u0000\u0207\u0209\u0005"+
		"B\u0000\u0000\u0208\u0207\u0001\u0000\u0000\u0000\u0208\u0209\u0001\u0000"+
		"\u0000\u0000\u0209\u020a\u0001\u0000\u0000\u0000\u020a\u020b\u0005\f\u0000"+
		"\u0000\u020b\u020c\u0003|>\u0000\u020ci\u0001\u0000\u0000\u0000\u020d"+
		"\u0212\u0003l6\u0000\u020e\u0212\u0003n7\u0000\u020f\u0212\u0003p8\u0000"+
		"\u0210\u0212\u0003r9\u0000\u0211\u020d\u0001\u0000\u0000\u0000\u0211\u020e"+
		"\u0001\u0000\u0000\u0000\u0211\u020f\u0001\u0000\u0000\u0000\u0211\u0210"+
		"\u0001\u0000\u0000\u0000\u0212k\u0001\u0000\u0000\u0000\u0213\u0214\u0005"+
		"S\u0000\u0000\u0214m\u0001\u0000\u0000\u0000\u0215\u0216\u0007\u0006\u0000"+
		"\u0000\u0216o\u0001\u0000\u0000\u0000\u0217\u0219\u0005E\u0000\u0000\u0218"+
		"\u021a\u0003f3\u0000\u0219\u0218\u0001\u0000\u0000\u0000\u0219\u021a\u0001"+
		"\u0000\u0000\u0000\u021a\u021b\u0001\u0000\u0000\u0000\u021b\u021c\u0005"+
		"F\u0000\u0000\u021cq\u0001\u0000\u0000\u0000\u021d\u0226\u0005C\u0000"+
		"\u0000\u021e\u0223\u0003t:\u0000\u021f\u0220\u0005H\u0000\u0000\u0220"+
		"\u0222\u0003t:\u0000\u0221\u021f\u0001\u0000\u0000\u0000\u0222\u0225\u0001"+
		"\u0000\u0000\u0000\u0223\u0221\u0001\u0000\u0000\u0000\u0223\u0224\u0001"+
		"\u0000\u0000\u0000\u0224\u0227\u0001\u0000\u0000\u0000\u0225\u0223\u0001"+
		"\u0000\u0000\u0000\u0226\u021e\u0001\u0000\u0000\u0000\u0226\u0227\u0001"+
		"\u0000\u0000\u0000\u0227\u0228\u0001\u0000\u0000\u0000\u0228\u0229\u0005"+
		"D\u0000\u0000\u0229s\u0001\u0000\u0000\u0000\u022a\u022b\u0003d2\u0000"+
		"\u022b\u022c\u0005J\u0000\u0000\u022c\u022d\u0003d2\u0000\u022du\u0001"+
		"\u0000\u0000\u0000\u022e\u022f\u0006;\uffff\uffff\u0000\u022f\u0230\u0005"+
		"@\u0000\u0000\u0230\u0235\u0001\u0000\u0000\u0000\u0231\u0232\n\u0001"+
		"\u0000\u0000\u0232\u0234\u0003x<\u0000\u0233\u0231\u0001\u0000\u0000\u0000"+
		"\u0234\u0237\u0001\u0000\u0000\u0000\u0235\u0233\u0001\u0000\u0000\u0000"+
		"\u0235\u0236\u0001\u0000\u0000\u0000\u0236w\u0001\u0000\u0000\u0000\u0237"+
		"\u0235\u0001\u0000\u0000\u0000\u0238\u0239\u0005E\u0000\u0000\u0239\u023a"+
		"\u0003d2\u0000\u023a\u023b\u0005F\u0000\u0000\u023b\u023f\u0001\u0000"+
		"\u0000\u0000\u023c\u023d\u0005K\u0000\u0000\u023d\u023f\u0003z=\u0000"+
		"\u023e\u0238\u0001\u0000\u0000\u0000\u023e\u023c\u0001\u0000\u0000\u0000"+
		"\u023fy\u0001\u0000\u0000\u0000\u0240\u0241\u0005@\u0000\u0000\u0241{"+
		"\u0001\u0000\u0000\u0000\u0242\u0243\u0005@\u0000\u0000\u0243\u0244\u0003"+
		"~?\u0000\u0244}\u0001\u0000\u0000\u0000\u0245\u0247\u0005A\u0000\u0000"+
		"\u0246\u0248\u0003f3\u0000\u0247\u0246\u0001\u0000\u0000\u0000\u0247\u0248"+
		"\u0001\u0000\u0000\u0000\u0248\u0249\u0001\u0000\u0000\u0000\u0249\u024a"+
		"\u0005B\u0000\u0000\u024a\u007f\u0001\u0000\u0000\u0000.\u0083\u009d\u00a3"+
		"\u00ad\u00b0\u00b4\u00b7\u00ba\u00cb\u00d4\u00db\u00e8\u00ec\u00f7\u0105"+
		"\u010c\u0119\u0121\u0158\u0169\u016d\u0175\u0179\u017e\u018a\u0198\u019f"+
		"\u01a2\u01a9\u01b1\u01c1\u01c5\u01d4\u01eb\u01f6\u01f8\u0200\u0204\u0208"+
		"\u0211\u0219\u0223\u0226\u0235\u023e\u0247";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}