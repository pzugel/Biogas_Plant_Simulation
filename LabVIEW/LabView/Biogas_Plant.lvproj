﻿<?xml version='1.0' encoding='UTF-8'?>
<Project Type="Project" LVVersion="19008000">
	<Property Name="CCSymbols" Type="Str"></Property>
	<Property Name="NI.LV.All.SourceOnly" Type="Bool">false</Property>
	<Property Name="NI.Project.Description" Type="Str"></Property>
	<Item Name="My Computer" Type="My Computer">
		<Property Name="CCSymbols" Type="Str"></Property>
		<Property Name="server.app.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.control.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.tcp.access" Type="Str"></Property>
		<Property Name="server.tcp.enabled" Type="Bool">false</Property>
		<Property Name="server.tcp.port" Type="Int">0</Property>
		<Property Name="server.tcp.serviceName" Type="Str"></Property>
		<Property Name="server.tcp.serviceName.default" Type="Str">My Computer/</Property>
		<Property Name="server.vi.access" Type="Str"></Property>
		<Property Name="server.vi.callsEnabled" Type="Bool">true</Property>
		<Property Name="server.vi.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.viscripting.showScriptingOperationsInContextHelp" Type="Bool">false</Property>
		<Property Name="server.viscripting.showScriptingOperationsInEditor" Type="Bool">false</Property>
		<Property Name="specify.custom.address" Type="Bool">false</Property>
		<Item Name="Biogas_plant_setup" Type="Folder">
			<Item Name="Controls" Type="Folder">
				<Item Name="add_button.ctl" Type="VI" URL="../controls/add_button.ctl"/>
				<Item Name="help_button.ctl" Type="VI" URL="../controls/help_button.ctl"/>
				<Item Name="hydrolyse_picButton.ctl" Type="VI" URL="../controls/hydrolyse_picButton.ctl"/>
				<Item Name="load_button.ctl" Type="VI" URL="../controls/load_button.ctl"/>
				<Item Name="methane_picButton.ctl" Type="VI" URL="../controls/methane_picButton.ctl"/>
				<Item Name="open_button.ctl" Type="VI" URL="../controls/open_button.ctl"/>
				<Item Name="open_picButton.ctl" Type="VI" URL="../controls/open_picButton.ctl"/>
				<Item Name="pause_button.ctl" Type="VI" URL="../controls/pause_button.ctl"/>
				<Item Name="plot_button.ctl" Type="VI" URL="../controls/plot_button.ctl"/>
				<Item Name="refresh_button.ctl" Type="VI" URL="../controls/refresh_button.ctl"/>
				<Item Name="run_button.ctl" Type="VI" URL="../controls/run_button.ctl"/>
				<Item Name="save_button.ctl" Type="VI" URL="../controls/save_button.ctl"/>
				<Item Name="stop_button.ctl" Type="VI" URL="../controls/stop_button.ctl"/>
				<Item Name="stop_sim_button.ctl" Type="VI" URL="../controls/stop_sim_button.ctl"/>
				<Item Name="storage_picButton.ctl" Type="VI" URL="../controls/storage_picButton.ctl"/>
				<Item Name="uncouple_button.ctl" Type="VI" URL="../controls/uncouple_button.ctl"/>
				<Item Name="validate_button.ctl" Type="VI" URL="../controls/validate_button.ctl"/>
			</Item>
			<Item Name="Elements" Type="Folder">
				<Item Name="SubVI" Type="Folder">
					<Item Name="feedback_run.vi" Type="VI" URL="../subVI/feedback_run.vi"/>
					<Item Name="hydrolysis_reactor_run.vi" Type="VI" URL="../subVI/hydrolysis_reactor_run.vi"/>
					<Item Name="hydrolysis_reactor_run_parallel.vi" Type="VI" URL="../subVI/hydrolysis_reactor_run_parallel.vi"/>
					<Item Name="hydrolysis_reactor_setup.vi" Type="VI" URL="../subVI/hydrolysis_reactor_setup.vi"/>
					<Item Name="methane_reactor_run.vi" Type="VI" URL="../subVI/methane_reactor_run.vi"/>
					<Item Name="methane_reactor_setup.vi" Type="VI" URL="../subVI/methane_reactor_setup.vi"/>
					<Item Name="methane_reactor_update.vi" Type="VI" URL="../subVI/methane_reactor_update.vi"/>
					<Item Name="reactor_merge.vi" Type="VI" URL="../subVI/reactor_merge.vi"/>
					<Item Name="storage_run.vi" Type="VI" URL="../subVI/storage_run.vi"/>
				</Item>
				<Item Name="feedback.vi" Type="VI" URL="../elements/feedback.vi"/>
				<Item Name="hydrolysis_reactor.vi" Type="VI" URL="../elements/hydrolysis_reactor.vi"/>
				<Item Name="hydrolysis_reactor_parallel.vi" Type="VI" URL="../elements/hydrolysis_reactor_parallel.vi"/>
				<Item Name="methane_reactor.vi" Type="VI" URL="../elements/methane_reactor.vi"/>
				<Item Name="storage.vi" Type="VI" URL="../elements/storage.vi"/>
			</Item>
			<Item Name="Libraries" Type="Folder">
				<Item Name="libCheckpointUpdate.lvlib" Type="Library" URL="../lib/libCheckpointUpdate/libCheckpointUpdate.lvlib"/>
				<Item Name="libFileFunctions.lvlib" Type="Library" URL="../lib/libFileFunctions/libFileFunctions.lvlib"/>
				<Item Name="libOutputReader.lvlib" Type="Library" URL="../lib/libOutputReader/libOutputReader.lvlib"/>
				<Item Name="libSimulationControl.lvlib" Type="Library" URL="../lib/libSimulationControl/libSimulationControl.lvlib"/>
				<Item Name="libSpecValiReader.lvlib" Type="Library" URL="../lib/libSpecValiReader/libSpecValiReader.lvlib"/>
			</Item>
			<Item Name="Obsolete Code" Type="Folder">
				<Item Name="hydrolyse_reactor_repetition_check.vi" Type="VI" URL="../subVI/hydrolyse_reactor_repetition_check.vi"/>
				<Item Name="main_getPh.vi" Type="VI" URL="../subVI/main_getPh.vi"/>
			</Item>
			<Item Name="Structures" Type="Folder">
				<Item Name="STRUCT_1_STAGE.vi" Type="VI" URL="../structures/STRUCT_1_STAGE.vi"/>
				<Item Name="STRUCT_2_STAGE.vi" Type="VI" URL="../structures/STRUCT_2_STAGE.vi"/>
				<Item Name="STRUCT_2_STAGE_PL.vi" Type="VI" URL="../structures/STRUCT_2_STAGE_PL.vi"/>
				<Item Name="STRUCT_3_STAGE.vi" Type="VI" URL="../structures/STRUCT_3_STAGE.vi"/>
				<Item Name="STRUCT_3_STAGE_PL.vi" Type="VI" URL="../structures/STRUCT_3_STAGE_PL.vi"/>
				<Item Name="STRUCT_SIMPLE_HYDROLYSIS.vi" Type="VI" URL="../structures/STRUCT_SIMPLE_HYDROLYSIS.vi"/>
			</Item>
			<Item Name="SubVI" Type="Folder">
				<Item Name="Element Editor" Type="Folder">
					<Item Name="element_editor.vi" Type="VI" URL="../subVI/element_editor.vi"/>
					<Item Name="element_editor_close.vi" Type="VI" URL="../subVI/element_editor_close.vi"/>
					<Item Name="element_editor_highlight_validation.vi" Type="VI" URL="../subVI/element_editor_highlight_validation.vi"/>
					<Item Name="element_editor_hydrolysis_initial.vi" Type="VI" URL="../subVI/element_editor_hydrolysis_initial.vi"/>
					<Item Name="element_editor_hydroysis_run.vi" Type="VI" URL="../subVI/element_editor_hydroysis_run.vi"/>
					<Item Name="element_editor_init_tree.vi" Type="VI" URL="../subVI/element_editor_init_tree.vi"/>
					<Item Name="element_editor_list_hydrolysis.vi" Type="VI" URL="../subVI/element_editor_list_hydrolysis.vi"/>
					<Item Name="element_editor_load_spec.vi" Type="VI" URL="../subVI/element_editor_load_spec.vi"/>
					<Item Name="element_editor_load_vali.vi" Type="VI" URL="../subVI/element_editor_load_vali.vi"/>
					<Item Name="element_editor_methane_initial.vi" Type="VI" URL="../subVI/element_editor_methane_initial.vi"/>
					<Item Name="element_editor_methane_run.vi" Type="VI" URL="../subVI/element_editor_methane_run.vi"/>
					<Item Name="element_editor_save.vi" Type="VI" URL="../subVI/element_editor_save.vi"/>
					<Item Name="element_editor_save_as.vi" Type="VI" URL="../subVI/element_editor_save_as.vi"/>
					<Item Name="element_editor_tree_control.vi" Type="VI" URL="../subVI/element_editor_tree_control.vi"/>
					<Item Name="element_editor_validate_specs.vi" Type="VI" URL="../subVI/element_editor_validate_specs.vi"/>
				</Item>
				<Item Name="Main Panel" Type="Folder">
					<Item Name="Environment" Type="Folder">
						<Item Name="main_clear_environment.vi" Type="VI" URL="../subVI/main_clear_environment.vi"/>
						<Item Name="main_create_environment.vi" Type="VI" URL="../subVI/main_create_environment.vi"/>
						<Item Name="main_load_environment_check.vi" Type="VI" URL="../subVI/main_load_environment_check.vi"/>
						<Item Name="main_load_environtment.vi" Type="VI" URL="../subVI/main_load_environtment.vi"/>
						<Item Name="main_update_env_tree.vi" Type="VI" URL="../subVI/main_update_env_tree.vi"/>
					</Item>
					<Item Name="main_auto_cleanup.vi" Type="VI" URL="../subVI/main_auto_cleanup.vi"/>
					<Item Name="main_break_signal.vi" Type="VI" URL="../subVI/main_break_signal.vi"/>
					<Item Name="main_console_output.vi" Type="VI" URL="../subVI/main_console_output.vi"/>
					<Item Name="main_feedback_sliders.vi" Type="VI" URL="../subVI/main_feedback_sliders.vi"/>
					<Item Name="main_feeding.vi" Type="VI" URL="../subVI/main_feeding.vi"/>
					<Item Name="main_init_element_specs.vi" Type="VI" URL="../subVI/main_init_element_specs.vi"/>
					<Item Name="main_init_panel.vi" Type="VI" URL="../subVI/main_init_panel.vi"/>
					<Item Name="main_init_reactor_tipstrip.vi" Type="VI" URL="../subVI/main_init_reactor_tipstrip.vi"/>
					<Item Name="main_kill_ugshell.vi" Type="VI" URL="../subVI/main_kill_ugshell.vi"/>
					<Item Name="main_panel_run_init.vi" Type="VI" URL="../subVI/main_panel_run_init.vi"/>
					<Item Name="main_prepare_run.vi" Type="VI" URL="../subVI/main_prepare_run.vi"/>
					<Item Name="main_reactor_display.vi" Type="VI" URL="../subVI/main_reactor_display.vi"/>
					<Item Name="main_reset_panel.vi" Type="VI" URL="../subVI/main_reset_panel.vi"/>
					<Item Name="main_run.vi" Type="VI" URL="../subVI/main_run.vi"/>
					<Item Name="main_runtime_display.vi" Type="VI" URL="../subVI/main_runtime_display.vi"/>
					<Item Name="main_set_panel.vi" Type="VI" URL="../subVI/main_set_panel.vi"/>
					<Item Name="main_set_simulation_file.vi" Type="VI" URL="../subVI/main_set_simulation_file.vi"/>
					<Item Name="main_sim_log_uncoupled.vi" Type="VI" URL="../subVI/main_sim_log_uncoupled.vi"/>
					<Item Name="main_time_control.vi" Type="VI" URL="../subVI/main_time_control.vi"/>
					<Item Name="main_write_summary.vi" Type="VI" URL="../subVI/main_write_summary.vi"/>
				</Item>
				<Item Name="Plant" Type="Folder">
					<Item Name="plant_pause.vi" Type="VI" URL="../subVI/plant_pause.vi"/>
					<Item Name="plant_stop.vi" Type="VI" URL="../subVI/plant_stop.vi"/>
					<Item Name="plant_structure.vi" Type="VI" URL="../subVI/plant_structure.vi"/>
				</Item>
				<Item Name="Plot" Type="Folder">
					<Property Name="NI.SortType" Type="Int">3</Property>
					<Item Name="Tree Control" Type="Folder">
						<Item Name="plot_tree_clear.vi" Type="VI" URL="../subVI/plot_tree_clear.vi"/>
						<Item Name="plot_context_menu.vi" Type="VI" URL="../subVI/plot_context_menu.vi"/>
						<Item Name="plot_tree_control.vi" Type="VI" URL="../subVI/plot_tree_control.vi"/>
						<Item Name="getchildren.vi" Type="VI" URL="../subVI/getchildren.vi"/>
					</Item>
					<Item Name="plot_close.vi" Type="VI" URL="../subVI/plot_close.vi"/>
					<Item Name="plot_display.vi" Type="VI" URL="../subVI/plot_display.vi"/>
					<Item Name="plot_init_panel.vi" Type="VI" URL="../subVI/plot_init_panel.vi"/>
					<Item Name="plot_load_sim_output.vi" Type="VI" URL="../subVI/plot_load_sim_output.vi"/>
					<Item Name="plot_main.vi" Type="VI" URL="../subVI/plot_main.vi"/>
					<Item Name="plot_merge_same_filenames.vi" Type="VI" URL="../subVI/plot_merge_same_filenames.vi"/>
					<Item Name="plot_read_csv.vi" Type="VI" URL="../subVI/plot_read_csv.vi"/>
					<Item Name="plot_read_output_file.vi" Type="VI" URL="../subVI/plot_read_output_file.vi"/>
					<Item Name="plot_run.vi" Type="VI" URL="../subVI/plot_run.vi"/>
				</Item>
			</Item>
		</Item>
		<Item Name="main.vi" Type="VI" URL="../main.vi"/>
		<Item Name="Dependencies" Type="Dependencies">
			<Item Name="vi.lib" Type="Folder">
				<Item Name="Application Directory.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/file.llb/Application Directory.vi"/>
				<Item Name="Assert Block Data Type.vim" Type="VI" URL="/&lt;vilib&gt;/Utility/TypeAssert/Assert Block Data Type.vim"/>
				<Item Name="Bit-array To Byte-array.vi" Type="VI" URL="/&lt;vilib&gt;/picture/pictutil.llb/Bit-array To Byte-array.vi"/>
				<Item Name="BuildHelpPath.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/BuildHelpPath.vi"/>
				<Item Name="Check Path.vi" Type="VI" URL="/&lt;vilib&gt;/picture/jpeg.llb/Check Path.vi"/>
				<Item Name="Check Special Tags.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Check Special Tags.vi"/>
				<Item Name="Check if File or Folder Exists.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/libraryn.llb/Check if File or Folder Exists.vi"/>
				<Item Name="Clear Errors.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Clear Errors.vi"/>
				<Item Name="Convert property node font to graphics font.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Convert property node font to graphics font.vi"/>
				<Item Name="Create Mask By Alpha.vi" Type="VI" URL="/&lt;vilib&gt;/picture/picture.llb/Create Mask By Alpha.vi"/>
				<Item Name="Details Display Dialog.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Details Display Dialog.vi"/>
				<Item Name="DialogType.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/DialogType.ctl"/>
				<Item Name="DialogTypeEnum.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/DialogTypeEnum.ctl"/>
				<Item Name="Directory of Top Level VI.vi" Type="VI" URL="/&lt;vilib&gt;/picture/jpeg.llb/Directory of Top Level VI.vi"/>
				<Item Name="Draw Flattened Pixmap.vi" Type="VI" URL="/&lt;vilib&gt;/picture/picture.llb/Draw Flattened Pixmap.vi"/>
				<Item Name="Draw Text at Point.vi" Type="VI" URL="/&lt;vilib&gt;/picture/picture.llb/Draw Text at Point.vi"/>
				<Item Name="Draw Text in Rect.vi" Type="VI" URL="/&lt;vilib&gt;/picture/picture.llb/Draw Text in Rect.vi"/>
				<Item Name="ErrWarn.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/ErrWarn.ctl"/>
				<Item Name="Error Cluster From Error Code.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Error Cluster From Error Code.vi"/>
				<Item Name="Error Code Database.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Error Code Database.vi"/>
				<Item Name="Escape Characters for HTTP.vi" Type="VI" URL="/&lt;vilib&gt;/printing/PathToURL.llb/Escape Characters for HTTP.vi"/>
				<Item Name="Find Tag.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Find Tag.vi"/>
				<Item Name="FixBadRect.vi" Type="VI" URL="/&lt;vilib&gt;/picture/pictutil.llb/FixBadRect.vi"/>
				<Item Name="Flatten Pixmap.vi" Type="VI" URL="/&lt;vilib&gt;/picture/pixmap.llb/Flatten Pixmap.vi"/>
				<Item Name="Format Message String.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Format Message String.vi"/>
				<Item Name="FormatTime String.vi" Type="VI" URL="/&lt;vilib&gt;/express/express execution control/ElapsedTimeBlock.llb/FormatTime String.vi"/>
				<Item Name="General Error Handler Core CORE.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/General Error Handler Core CORE.vi"/>
				<Item Name="General Error Handler.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/General Error Handler.vi"/>
				<Item Name="Get File Extension.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/libraryn.llb/Get File Extension.vi"/>
				<Item Name="Get String Text Bounds.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Get String Text Bounds.vi"/>
				<Item Name="Get System Directory.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/sysdir.llb/Get System Directory.vi"/>
				<Item Name="Get Text Rect.vi" Type="VI" URL="/&lt;vilib&gt;/picture/picture.llb/Get Text Rect.vi"/>
				<Item Name="GetHelpDir.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/GetHelpDir.vi"/>
				<Item Name="GetRTHostConnectedProp.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/GetRTHostConnectedProp.vi"/>
				<Item Name="Is Path and Not Empty.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/file.llb/Is Path and Not Empty.vi"/>
				<Item Name="LVBoundsTypeDef.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/miscctls.llb/LVBoundsTypeDef.ctl"/>
				<Item Name="LVPositionTypeDef.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/miscctls.llb/LVPositionTypeDef.ctl"/>
				<Item Name="LVRectTypeDef.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/miscctls.llb/LVRectTypeDef.ctl"/>
				<Item Name="List Directory and LLBs.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/libraryn.llb/List Directory and LLBs.vi"/>
				<Item Name="Longest Line Length in Pixels.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Longest Line Length in Pixels.vi"/>
				<Item Name="NI_FileType.lvlib" Type="Library" URL="/&lt;vilib&gt;/Utility/lvfile.llb/NI_FileType.lvlib"/>
				<Item Name="NI_Matrix.lvlib" Type="Library" URL="/&lt;vilib&gt;/Analysis/Matrix/NI_Matrix.lvlib"/>
				<Item Name="NI_PackedLibraryUtility.lvlib" Type="Library" URL="/&lt;vilib&gt;/Utility/LVLibp/NI_PackedLibraryUtility.lvlib"/>
				<Item Name="Not Found Dialog.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Not Found Dialog.vi"/>
				<Item Name="Open URL in Default Browser (path).vi" Type="VI" URL="/&lt;vilib&gt;/Platform/browser.llb/Open URL in Default Browser (path).vi"/>
				<Item Name="Open URL in Default Browser (string).vi" Type="VI" URL="/&lt;vilib&gt;/Platform/browser.llb/Open URL in Default Browser (string).vi"/>
				<Item Name="Open URL in Default Browser core.vi" Type="VI" URL="/&lt;vilib&gt;/Platform/browser.llb/Open URL in Default Browser core.vi"/>
				<Item Name="Open URL in Default Browser.vi" Type="VI" URL="/&lt;vilib&gt;/Platform/browser.llb/Open URL in Default Browser.vi"/>
				<Item Name="PCT Pad String.vi" Type="VI" URL="/&lt;vilib&gt;/picture/picture.llb/PCT Pad String.vi"/>
				<Item Name="Path to URL inner.vi" Type="VI" URL="/&lt;vilib&gt;/printing/PathToURL.llb/Path to URL inner.vi"/>
				<Item Name="Path to URL.vi" Type="VI" URL="/&lt;vilib&gt;/printing/PathToURL.llb/Path to URL.vi"/>
				<Item Name="Read PNG File.vi" Type="VI" URL="/&lt;vilib&gt;/picture/png.llb/Read PNG File.vi"/>
				<Item Name="Recursive File List.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/libraryn.llb/Recursive File List.vi"/>
				<Item Name="Search and Replace Pattern.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Search and Replace Pattern.vi"/>
				<Item Name="Set Bold Text.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Set Bold Text.vi"/>
				<Item Name="Set Cursor (Cursor ID).vi" Type="VI" URL="/&lt;vilib&gt;/Utility/cursorutil.llb/Set Cursor (Cursor ID).vi"/>
				<Item Name="Set Cursor (Icon Pict).vi" Type="VI" URL="/&lt;vilib&gt;/Utility/cursorutil.llb/Set Cursor (Icon Pict).vi"/>
				<Item Name="Set Cursor.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/cursorutil.llb/Set Cursor.vi"/>
				<Item Name="Set String Value.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Set String Value.vi"/>
				<Item Name="Sort 2D Array - Pop Stack.vi" Type="VI" URL="/&lt;vilib&gt;/Array/Sort 2D Array - Pop Stack.vi"/>
				<Item Name="Sort 2D Array - Push Stack.vi" Type="VI" URL="/&lt;vilib&gt;/Array/Sort 2D Array - Push Stack.vi"/>
				<Item Name="Sort 2D Array.vim" Type="VI" URL="/&lt;vilib&gt;/Array/Sort 2D Array.vim"/>
				<Item Name="Space Constant.vi" Type="VI" URL="/&lt;vilib&gt;/dlg_ctls.llb/Space Constant.vi"/>
				<Item Name="System Directory Type.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/sysdir.llb/System Directory Type.ctl"/>
				<Item Name="System Exec.vi" Type="VI" URL="/&lt;vilib&gt;/Platform/system.llb/System Exec.vi"/>
				<Item Name="TagReturnType.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/TagReturnType.ctl"/>
				<Item Name="Three Button Dialog CORE.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Three Button Dialog CORE.vi"/>
				<Item Name="Three Button Dialog.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Three Button Dialog.vi"/>
				<Item Name="Trim Whitespace.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Trim Whitespace.vi"/>
				<Item Name="Unflatten Pixmap.vi" Type="VI" URL="/&lt;vilib&gt;/picture/pixmap.llb/Unflatten Pixmap.vi"/>
				<Item Name="eventvkey.ctl" Type="VI" URL="/&lt;vilib&gt;/event_ctls.llb/eventvkey.ctl"/>
				<Item Name="ex_CorrectErrorChain.vi" Type="VI" URL="/&lt;vilib&gt;/express/express shared/ex_CorrectErrorChain.vi"/>
				<Item Name="imagedata.ctl" Type="VI" URL="/&lt;vilib&gt;/picture/picture.llb/imagedata.ctl"/>
				<Item Name="subElapsedTime.vi" Type="VI" URL="/&lt;vilib&gt;/express/express execution control/ElapsedTimeBlock.llb/subElapsedTime.vi"/>
				<Item Name="subFile Dialog.vi" Type="VI" URL="/&lt;vilib&gt;/express/express input/FileDialogBlock.llb/subFile Dialog.vi"/>
				<Item Name="whitespace.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/whitespace.ctl"/>
			</Item>
		</Item>
		<Item Name="Build Specifications" Type="Build"/>
	</Item>
</Project>
