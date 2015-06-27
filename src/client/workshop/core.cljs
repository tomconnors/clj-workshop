(ns workshop.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [re-frame.middleware :as m]
            [re-frame.handlers])
  (:require-macros [reagent.ratom :refer [reaction]]))

(enable-console-print!)

;;; Subscription Fns

(defn register-subs [& name-sub-pairs]
  (doseq [[name handler] (partition 2 name-sub-pairs)]
    (re-frame/register-sub name handler)))

(register-subs
 :initialized? (fn [db] (reaction (not (empty? @db))))
 :todos (fn [db] (reaction (:todos @db))))

(aset js/window "appState" (fn [] (prn @re-frame.db/app-db)))

;;; Handler Fns

(defn register-handlers [handlers-map]
  (doseq [[name handler] handlers-map]
    (re-frame.handlers/register-base name handler)))

(let [counter (atom 0)]
  (defn fake-uuid []
    (swap! counter inc)))

(def handlers
  {:initialize-db (m/pure (fn [_ _]
                            {:todos {}}))
   :add-todo (m/pure (fn [db [_ todo]]
                       (assoc-in db [:todos (fake-uuid)] todo)))})

(register-handlers handlers)

;;; Components

;; need to pick a great header that will really knock everyone's socks off.
(defn header []
  [:h1 "TODO"])

;; need to be able to delete todos!
(defn todo-list []
  (let [todos-ref (re-frame/subscribe [:todos])]
    (fn []
      [:div (map (fn [[id text]]
                   [:div.todo-item text])
                 @todos-ref)])))

(defn enter-keypress? [e]
  (= (.-which e) 13))

;; need to implement adding todo functionality
(defn blank-todo []
  (let [component (reagent/current-component)]
    [:input {:type "text"
             ;; add keypress handling
             :on-key-press (fn [e] (if (enter-keypress? e) (.blur (.getDOMNode component))))
             ;; add on-blur handling
             :on-blur (fn [e] (re-frame/dispatch [:add-todo (aget e "target" "value")])
                        (aset e "target" "value" ""))
             }]))

(defn body []
  [:div
   [header]
   [blank-todo]
   [todo-list]])

(defn page []
  (let [initialized? (re-frame/subscribe [:initialized?])]
    (fn []
      (if @initialized?
        [body]
        [:div "No ready yet..."]))))

(defn render []
  (reagent/render [page]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (render))

(re-frame/dispatch [:initialize-db])
