package org.apache.lucene.queryparser.flexible.standard;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.MockAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.LuceneTestCase;

/**
 */
public class TextRegexpProcessing extends LuceneTestCase {

  public void testRegexQueryParsing() throws Exception {
    final String[] fields = {"b", "t"};

    final StandardQueryParser parser = new StandardQueryParser();
    parser.setMultiFields(fields);
    parser.setDefaultOperator(StandardQueryConfigHandler.Operator.AND);
    parser.setAnalyzer(new MockAnalyzer(random()));

    BooleanQuery exp = new BooleanQuery();
    exp.add(new BooleanClause(new RegexpQuery(new Term("b", "ab.+")), BooleanClause.Occur.MUST));
    exp.add(new BooleanClause(new RegexpQuery(new Term("t", "ab.+")), BooleanClause.Occur.MUST));

    assertEquals(exp, parser.parse("/ab.+/", null));

    RegexpQuery regexpQueryexp = new RegexpQuery(new Term("test", "[abc]?[0-9]"));

    assertEquals(regexpQueryexp, parser.parse("test:/[abc]?[0-9]/", null));

  }

}
