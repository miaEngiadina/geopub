;; Copyright © 2019, 2020 pukkamustard <pukkamustard@posteo.net>
;;
;; This file is part of GeoPub.
;;
;; GeoPub is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.
;;
;; GeoPub is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.
;;
;; You should have received a copy of the GNU General Public License
;; along with GeoPub.  If not, see <https://www.gnu.org/licenses/>.

(ns geopub.ui.activity
  (:require [geopub.ns :refer [as schema]]
            [geopub.state]
            [geopub.data.rdf :refer [iri-component
                                     description-component
                                     description-icon-src
                                     description-label-component
                                     description-created-at]]
            [geopub.data.activity :as activity]
            [rdf.core :as rdf]
            [rdf.ns :refer [rdf rdfs]]
            [rdf.graph.map]
            ["date-fns" :as date-fns]))


(defn published-component [activity]
  (let [published (description-created-at activity)]
    [:span (.formatDistance date-fns published (new js/Date)
                            (clj->js {:addSuffix true}))]))

(defn activity-component [activity]
  (let
      [object (rdf/description-move
               activity
               (first (rdf/description-get activity (as "object"))))
       actor (rdf/description-move
              activity
              (first (rdf/description-get activity (as "actor"))))

       activity-type (rdf/description-move
                      activity
                      (first (rdf/description-get activity (rdf :type))))]
      [:div.activity
       (if-let [icon-src (description-icon-src actor)]
         [:span.icon [:img {:src (rdf/iri-value icon-src)}]])

       [:span.actor [description-label-component actor]]
       [:span.activity-type [description-label-component activity]]
       [:span.object [description-label-component object]]
       [published-component activity]]))


(defn activity-timeline-component [activities]
  [:div.timeline
   (for [activity activities]
     ^{:key (hash activity)}
     [activity-component activity])])

(defn sidebar []
  [:div.sidebar
   [:nav
    [:p "TODO here you can filter activities"]
    [:ul
     [:li [:a {:href "#"} "you"]]
     [:li [:a {:href "#"} "people you follow"]]
     [:li [:a {:href "#"} "group xyz"]]
     [:li [:a {:href "#"} "all"]]
     ]]])

(defn view [state]
  [:div.ui-page
   ;; [sidebar]
   [:main
    [:h1 "Activity"]
    [activity-timeline-component
     (activity/get-activities (:graph @state))]]])
