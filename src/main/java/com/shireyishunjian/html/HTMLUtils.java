package com.shireyishunjian.html;

import org.jsoup.nodes.Document;

public class HTMLUtils {
    final static String head= """
              <meta charset="utf-8">
              <meta property="og:image" content="./data/avatar/noavatar.svg">
              <base href="https://www.shireyishunjian.com/main/">
              <script src="data/cache/common.js" type="text/javascript"></script>
              <script src="data/cache/forum.js" type="text/javascript"></script>
              <script src="data/cache/forum_viewthread.js" type="text/javascript"></script>
              <script type="text/javascript">
                window.onload = function() {
                    new lazyload()
                };
              </script>
              <link rel="stylesheet" type="text/css" href="data/cache/style_25_common.css">
              <link rel="stylesheet" type="text/css" href="data/cache/style_25_forum_viewthread.css">
              <style>
                table {
                    border-collapse: separate;
                    text-indent: initial;
                    line-height: normal;
                    font-weight: normal;
                    font-size: medium;
                    font-style: normal;
                    color: -internal-quirk-inherit;
                    text-align: start;
                    border-spacing: 2px;
                    white-space: normal;
                    font-variant: normal;
                }
              </style>
            """;

    public static void addHead(Document document){
        document.head().append(head);
    }
}
