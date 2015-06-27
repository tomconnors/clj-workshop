(defproject workshop "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0-RC2"]
                 [org.clojure/clojurescript "0.0-3308"]
                 [reagent "0.5.0"]
                 [re-frame "0.4.1"]]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.3" :exclusions [cider/cider-nrepl]]
            [com.palletops/lein-shorthand "0.4.0"]]

  :shorthand {. {pp clojure.pprint/pprint
                 load-project alembic.still/load-project
                 doc clojure.repl/doc
                 source clojure.repl/source
                 pprint clojure.pprint/pprint
                 ppt clojure.pprint/pprint-table}}

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/client"]

                        :figwheel {:on-jsload "workshop.core/main"
                                   :server-port 3449
                                   :repl true
                                   :nrepl-port 7888}

                        :compiler {:main workshop.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true}
                        :notify-command ["./sh/notify"]}

                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:main workshop.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]})
