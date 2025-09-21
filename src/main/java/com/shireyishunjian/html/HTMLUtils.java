package com.shireyishunjian.html;

import org.jsoup.nodes.Document;

public class HTMLUtils {
    final static String head= """
              <meta charset="utf-8">
              <meta property="og:image" content="./data/avatar/noavatar.svg">
              <base href="https://www.shireyishunjian.com/main/">
              <script src="data/cache/common.js" type="text/javascript"></script>
              <script src="data/cache/forum.js" type="text/javascript"></script>
              <link rel="stylesheet" type="text/css" href="data/cache/style_25_common.css">
              <link rel="stylesheet" type="text/css" href="data/cache/style_25_forum_viewthread.css">
            """;

    public static void addHead(Document document){
        document.head().append(head);
    }
}
