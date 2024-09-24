package org.sourceforge.kga.gui.analyze;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.sourceforge.kga.DatedPoint;
import org.sourceforge.kga.Plant;
import org.sourceforge.kga.SeedEntry;
import org.sourceforge.kga.TaxonVariety;
import org.sourceforge.kga.analyze.FXField;
import org.sourceforge.kga.analyze.Query;
import org.sourceforge.kga.analyze.QueryField;
import org.sourceforge.kga.analyze.QueryProvider;
import org.sourceforge.kga.gui.CentralWindowProvider;
import org.sourceforge.kga.gui.FileWithChanges.Listener;
import org.sourceforge.kga.gui.Printable;
import org.sourceforge.kga.gui.ProjectFileWithChanges;
import org.sourceforge.kga.gui.gardenplan.analysisQuery.GardenAnalysisQueryProvider;
import org.sourceforge.kga.gui.tableRecords.seedlistmanager.SeedListQueryProvider;
import org.sourceforge.kga.translation.Translation;
import org.sourceforge.kga.translation.Translation.Key;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AnalysisPane implements CentralWindowProvider {
	BorderPane mainpane;
	Stage primaryStage;
	ProjectFileWithChanges project;
	DelayedQueryTable results;
	BorderPane pivotBox;
	SplitPane leftPanel;
	QueryTypeDependantHolder<?> holder;
	Listener myListener;
	
	public AnalysisPane(Stage primaryStage, ProjectFileWithChanges project) {
		this.primaryStage=primaryStage;
		this.project=project;
	}

	@Override
	public Key getName() {
		return Translation.Key.analyze;
	}

	@Override
	public Node getLeftPane() {
		// TODO Auto-generated method stub
		return leftPanel;
	}

	@Override
	public Node getMainPane() {
		return mainpane;
	}

	@Override
	public Node getToolbar() {
		return pivotBox;
	}

	@Override
	public void onHide() {
		mainpane=null;
		results=null;
		pivotBox=null;
		leftPanel=null;
		holder=null;
		project.removeListener(myListener);
		myListener=null;
	}

	@Override
	public void onShow() {
		mainpane = new BorderPane();
		pivotBox = new BorderPane();
		results = new DelayedQueryTable();
		VBox QueryControls = new VBox();
		TreeView<providerGenerator<?>> providerSelector = new TreeView<providerGenerator<?>>(new TreeItem<providerGenerator<?>>());
		providerSelector.setShowRoot(false);
		providerSelector.getSelectionModel().selectedItemProperty().addListener((observable,old,newVal)->{
			holder = new QueryTypeDependantHolder<>(newVal.getValue(), QueryControls);			
		});
		providerSelector.getRoot().getChildren().add(new TreeItem<>(new providerGenerator<Entry<DatedPoint,TaxonVariety<Plant>>>() {

			@Override
			public QueryProvider<Entry<DatedPoint, TaxonVariety<Plant>>, FXField<Entry<DatedPoint, TaxonVariety<Plant>>, ?>> getQueryProvider() {
				return new GardenAnalysisQueryProvider(project);
			}
			
			@Override
			public String toString() {
				return Translation.getCurrent().action_garden_statistics();
			}
			
		}));
		providerSelector.getRoot().getChildren().add(new TreeItem<>(new providerGenerator<Entry<String,SeedEntry>>() {

			@Override
			public QueryProvider<Entry<String,SeedEntry>,FXField<Entry<String,SeedEntry>,?>> getQueryProvider() {
				return new SeedListQueryProvider(project.getProject().getSeedCollection());
			}
			
			@Override
			public String toString() {
				return Translation.getCurrent().action_seed_manager();
			}
			
		}));
		providerSelector.getSelectionModel().select(0);
		leftPanel = new SplitPane(providerSelector,QueryControls);
		leftPanel.setOrientation(Orientation.VERTICAL);
		mainpane.setCenter(results);//new QueryFXTable<Entry<DatedPoint,TaxonVariety<Plant>>>(gardenStatisticsQuery));
		GardenAnalysisQueryProvider provider = new GardenAnalysisQueryProvider(project);
		//holder = new QueryTypeDependantHolder<>(()->{return new GardenAnalysisQueryProvider(project.getGarden()); });
		myListener = new Listener() {

			@Override
			public void objectChanged() {
				holder.updateProviderKeepQuery();				
			}

			@Override
			public void unsavedChangesChanged() {}
			
		};
		project.addListener(myListener);
	}

	@Override
	public Printable getPrintTask() {
		return null;
	}
	
	public interface providerGenerator <T> {
		public QueryProvider <T,FXField<T,?>> getQueryProvider();
	}
	
	public class QueryTypeDependantHolder <T> {
		PivotCombo<T> pivot;
		Query<T,FXField<T,?>> lastQuery;
		QueryProvider <T,FXField<T,?>> provider;
		FieldSelectionList<T> aggregateBy;
		FieldSelectionList<T> metrics;
		providerGenerator<T> providerMaker;
		
		public void updateProviderKeepQuery() {
			this.provider= providerMaker.getQueryProvider();
			changeQuery(new Query<T,FXField<T,?>>(lastQuery.getToAggregate(),lastQuery.getAggregateBy(),provider,lastQuery.getSortBy(),lastQuery.getPivotBy()));
		}
		
		public QueryTypeDependantHolder(providerGenerator<T> providerMaker, VBox QueryControls) {
			QueryControls.getChildren().clear();
			this.provider=providerMaker.getQueryProvider();
			this.providerMaker=providerMaker;
			aggregateBy = new FieldSelectionList<>((f)->{
				boolean exists = lastQuery.getAggregateBy().contains(f);
				SimpleBooleanProperty observable = new SimpleBooleanProperty(exists);
				observable.addListener((o,old,n)->{
					if(n==old) {
						return;
					}
					Collection <FXField<T,?>> newAggregate = new LinkedList< >(lastQuery.getAggregateBy());
					if (n)
						newAggregate.add((FXField<T,?>)f);
					else
						newAggregate.remove(f);
					changeQuery(new Query<T,FXField<T,?>>(lastQuery.getToAggregate(),newAggregate,provider,lastQuery.getSortBy(),lastQuery.getPivotBy()));
				});
				return observable;
			});
			aggregateBy.updateFields(getAggregationFields());
			metrics = new FieldSelectionList<>((f)->{
				boolean exists = lastQuery.getToAggregate().contains(f);
				SimpleBooleanProperty observable = new SimpleBooleanProperty(exists);
				observable.addListener((o,old,n)->{
					if(n==old) {
						return;
					}
					Collection <FXField<T,?>> newMetrics = new LinkedList< >(lastQuery.getToAggregate());
					if (n)
						newMetrics.add((FXField<T,?>)f);
					else
						newMetrics.remove(f);
					changeQuery(new Query<T,FXField<T,?>>(newMetrics,lastQuery.getAggregateBy(),provider,lastQuery.getSortBy(),lastQuery.getPivotBy()));
				});
				return observable;
			});
			metrics.updateFields(getMetricFields());
			changeQuery(provider.getDefaultQuery());
			
			Label gbLabel=new Label(Translation.getCurrent().analytics_groupby());
			VBox.setMargin(gbLabel, new Insets(5,5,0,5));
			QueryControls.getChildren().add(gbLabel);
			VBox.setMargin(aggregateBy, new Insets(0,5,10,5));
			QueryControls.getChildren().add(aggregateBy);

			Label metricLabel=new Label(Translation.getCurrent().analytics_metrics());
			VBox.setMargin(metricLabel, new Insets(5,5,0,5));
			QueryControls.getChildren().add(metricLabel);
			VBox.setMargin(metrics, new Insets(0,5,10,5));
			QueryControls.getChildren().add(metrics);
		}

		private Collection<FXField<T,?>> getAggregationFields() {

			Collection<FXField<T,?>> fields = new LinkedList<>();
			for(FXField<T,?>field  : provider.getAvailableFields()) {
				if (field.canAggregateBy()) {
					fields.add(field);
				}
			}
			return fields;
		}

		private Collection<FXField<T,?>> getMetricFields() {

			Collection<FXField<T,?>> fields = new LinkedList<>();
			for(FXField<T,?>field  : provider.getAvailableFields()) {
				if (field.getAllowedAggregations()!=QueryField.ALLOWED_AGGREGATIONS.NONE) {
					fields.add(field);
				}
			}
			return fields;
		}
		
		private void changeQuery(Query<T,FXField<T,?>> newQuery) {
			lastQuery=newQuery;
			pivot = new PivotCombo<T>(results,this);
			results.updateQuery( newQuery);
			
			pivotBox.setRight(pivot);
			BorderPane.setMargin(pivot, new Insets(5));
		}
	}

}
