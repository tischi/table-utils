package headless;

import de.embl.cba.tables.TableRows;
import de.embl.cba.tables.annotate.Annotator;
import de.embl.cba.tables.color.*;
import de.embl.cba.tables.morpholibj.ExploreMorphoLibJLabelImage;
import de.embl.cba.tables.tablerow.TableRowImageSegment;
import de.embl.cba.tables.view.TableRowsTableView;
import de.embl.cba.tables.view.combined.SegmentsTableAndBdvViews;
import ij.IJ;
import ij.ImagePlus;
import net.imagej.ImageJ;

import java.util.List;

public class AnnotateBlobs2D
{
	public static void main( String[] args )
	{
		final SegmentsTableAndBdvViews views = getViews();

		final TableRowsTableView< TableRowImageSegment > tableView
				= views.getTableRowsTableView();

		final String annotationColumnName = "Annotation";
		tableView.addColumn( annotationColumnName, "None" );

		final List< TableRowImageSegment > tableRows = tableView.getTableRows();

		TableRows.assignValue(
				annotationColumnName,
				tableRows.get( 0 ),
				"AAA",
				tableView.getTable() );

		TableRows.assignValue(
				annotationColumnName,
				tableRows.get( 1 ),
				"BBB",
				tableView.getTable() );

		final CategoryTableRowColumnColoringModel< TableRowImageSegment > coloringModel
				= new ColumnColoringModelCreator< TableRowImageSegment >( tableView.getTable()
		).createCategoricalColoringModel( "Annotation", false );

		views.getSelectionColoringModel().setColoringModel( coloringModel );
		views.getSelectionColoringModel().setSelectionMode( SelectionColoringModel.SelectionMode.SelectionColor );


		final Annotator annotator = new Annotator(
				annotationColumnName,
				tableRows,
				tableView.getTable(),
				views.getSelectionModel(),
				coloringModel
		);

		annotator.showDialog();



//		Tables.saveTable( tableView.getTable(),
//				new File( "/Users/tischer/Desktop/annotated_blobs.txt") );

	}

	public static SegmentsTableAndBdvViews getViews()
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ImagePlus intensities =
				IJ.openImage( RunExploreMLJSegmentation2D.class.getResource(
				"../blobs.zip" ).getFile() );

		final ImagePlus labels = IJ.openImage( RunExploreMLJSegmentation2D.class.getResource(
				"../mask-lbl.zip" ).getFile() );

		IJ.open( RunExploreMLJSegmentation2D.class.getResource(
				"../blobs-lbl-Morphometry.csv" ).getFile() );

		final ExploreMorphoLibJLabelImage explore =
				new ExploreMorphoLibJLabelImage(
						intensities, labels, "Results" );

		final SegmentsTableAndBdvViews views = explore.getTableAndBdvViews();

		return views;
	}
}
