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
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.LuceneTestCase;

/**
 */
public class TextRegexpProcessing extends LuceneTestCase {

  public void testRegexQueryParsing() throws Exception {
    final String[] fields = {"b", "t"};

    final StandardQueryParser parser = new StandardQueryParser();
    parser.setMultiFields(fields);
    parser.setAnalyzer(new MockAnalyzer(random()));

    Query q = parser.parse("/ab.+/", null);
    assertEquals("b:ab.+ t:ab.+", q.toString());

    q = parser.parse("test:/[abc]?[0-9]/", null);
    assertEquals("test:[abc]?[0-9]", q.toString());

  }

  public void testRegexQueryFinding() throws Exception {

    // prepare the index
    final Analyzer analyzer = new MockAnalyzer(random());
    final Directory ramDir = newDirectory();
    final IndexWriter iw = new IndexWriter(ramDir, newIndexWriterConfig
        (TEST_VERSION_CURRENT, analyzer));

    Document doc = new Document();
    doc.add(newField("body", "Definitiv ein 2012", TextField.TYPE_UNSTORED));
    iw.addDocument(doc);

    doc = new Document();
    doc.add(newField("body", "Definitiv kein 2013", TextField.TYPE_UNSTORED));
    iw.addDocument(doc);

    iw.close();

    // the index searcher
    final IndexReader ir = DirectoryReader.open(ramDir);
    final IndexSearcher is = new IndexSearcher(ir);

    // prepare the parser
    final StandardQueryParser parser = new StandardQueryParser();
    parser.setAnalyzer(analyzer);
    parser.setDefaultOperator(StandardQueryConfigHandler.Operator.AND);

    // search for "2012"
    Query q = parser.parse("body:/2012/", null);
    ScoreDoc[] hits = is.search(q, null, 1000).scoreDocs;
    assertEquals(1, hits.length);

    // search for "2013"
    q = parser.parse("body:/2013/", null);
    hits = is.search(q, null, 1000).scoreDocs;
    assertEquals(1, hits.length);

    // search for 201[23]
    q = parser.parse("body:/201[23]/", null);
    hits = is.search(q, null, 1000).scoreDocs;
    assertEquals(2, hits.length);

    // search for [k]?ein
    q = parser.parse("body:/[k]?ein/", null);
    hits = is.search(q, null, 1000).scoreDocs;
    assertEquals(2, hits.length);

    ir.close();
    ramDir.close();

  }

}
