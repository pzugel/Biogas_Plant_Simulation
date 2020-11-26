<?xml version='1.0' encoding='UTF-8'?>
<Project Type="Project" LVVersion="19008000">
	<Property Name="CCSymbols" Type="Str">PLANT_STRUCT,3_Stage;</Property>
	<Property Name="NI.LV.All.SourceOnly" Type="Bool">false</Property>
	<Property Name="NI.Project.Description" Type="Str"></Property>
	<Item Name="My Computer" Type="My Computer">
		<Property Name="server.app.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.control.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.tcp.enabled" Type="Bool">false</Property>
		<Property Name="server.tcp.port" Type="Int">0</Property>
		<Property Name="server.tcp.serviceName" Type="Str">My Computer/</Property>
		<Property Name="server.tcp.serviceName.default" Type="Str">My Computer/</Property>
		<Property Name="server.vi.callsEnabled" Type="Bool">true</Property>
		<Property Name="server.vi.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="specify.custom.address" Type="Bool">false</Property>
		<Item Name="Biogas_plant_setup" Type="Folder">
			<Item Name="Controls" Type="Folder">
				<Item Name="help_button.ctl" Type="VI" URL="../controls/help_button.ctl"/>
				<Item Name="load_button.ctl" Type="VI" URL="../controls/load_button.ctl"/>
				<Item Name="open_button.ctl" Type="VI" URL="../controls/open_button.ctl"/>
				<Item Name="pause_button.ctl" Type="VI" URL="../controls/pause_button.ctl"/>
				<Item Name="plot_button.ctl" Type="VI" URL="../controls/plot_button.ctl"/>
				<Item Name="refresh_button.ctl" Type="VI" URL="../controls/refresh_button.ctl"/>
				<Item Name="run_button.ctl" Type="VI" URL="../controls/run_button.ctl"/>
				<Item Name="save_button.ctl" Type="VI" URL="../controls/save_button.ctl"/>
				<Item Name="stop_button.ctl" Type="VI" URL="../controls/stop_button.ctl"/>
				<Item Name="stop_sim_button.ctl" Type="VI" URL="../controls/stop_sim_button.ctl"/>
				<Item Name="uncouple_button.ctl" Type="VI" URL="../controls/uncouple_button.ctl"/>
				<Item Name="validate_button.ctl" Type="VI" URL="../controls/validate_button.ctl"/>
			</Item>
			<Item Name="Libraries" Type="Folder">
				<Item Name="libFileFunctions.lvlib" Type="Library" URL="../lib/libFileFunctions/libFileFunctions.lvlib"/>
				<Item Name="libOutputReader.lvlib" Type="Library" URL="../lib/libOutputReader/libOutputReader.lvlib"/>
				<Item Name="libSimulationControl.lvlib" Type="Library" URL="../lib/libSimulationControl/libSimulationControl.lvlib"/>
				<Item Name="libSpecValiReader.lvlib" Type="Library" URL="../lib/libSpecValiReader/libSpecValiReader.lvlib"/>
			</Item>
			<Item Name="SubVI" Type="Folder">
				<Item Name="Initializer" Type="Folder">
					<Item Name="initializer_close.vi" Type="VI" URL="../subVI/initializer_close.vi"/>
					<Item Name="initializer_highlight_validation.vi" Type="VI" URL="../subVI/initializer_highlight_validation.vi"/>
					<Item Name="initializer_init_controls.vi" Type="VI" URL="../subVI/initializer_init_controls.vi"/>
					<Item Name="initializer_init_panel.vi" Type="VI" URL="../subVI/initializer_init_panel.vi"/>
					<Item Name="initializer_load_sim.vi" Type="VI" URL="../subVI/initializer_load_sim.vi"/>
					<Item Name="initializer_load_spec.vi" Type="VI" URL="../subVI/initializer_load_spec.vi"/>
					<Item Name="initializer_load_vali.vi" Type="VI" URL="../subVI/initializer_load_vali.vi"/>
					<Item Name="initializer_output.vi" Type="VI" URL="../subVI/initializer_output.vi"/>
					<Item Name="initializer_prepare_specfile.vi" Type="VI" URL="../subVI/initializer_prepare_specfile.vi"/>
					<Item Name="initializer_run.vi" Type="VI" URL="../subVI/initializer_run.vi"/>
					<Item Name="initializer_save_spec.vi" Type="VI" URL="../subVI/initializer_save_spec.vi"/>
					<Item Name="initializer_set_time.vi" Type="VI" URL="../subVI/initializer_set_time.vi"/>
					<Item Name="initializer_tree_control.vi" Type="VI" URL="../subVI/initializer_tree_control.vi"/>
					<Item Name="initializer_validate_specs.vi" Type="VI" URL="../subVI/initializer_validate_specs.vi"/>
				</Item>
				<Item Name="Main Panel" Type="Folder">
					<Item Name="main_break_signal.vi" Type="VI" URL="../subVI/main_break_signal.vi"/>
					<Item Name="main_impressum.vi" Type="VI" URL="../subVI/main_impressum.vi"/>
					<Item Name="main_init_panel.vi" Type="VI" URL="../subVI/main_init_panel.vi"/>
					<Item Name="main_kill_ugshell.vi" Type="VI" URL="../subVI/main_kill_ugshell.vi"/>
					<Item Name="main_reactor_display.vi" Type="VI" URL="../subVI/main_reactor_display.vi"/>
					<Item Name="main_reset_panel.vi" Type="VI" URL="../subVI/main_reset_panel.vi"/>
					<Item Name="main_run.vi" Type="VI" URL="../subVI/main_run.vi"/>
					<Item Name="main_set_panel.vi" Type="VI" URL="../subVI/main_set_panel.vi"/>
					<Item Name="main_sim_log_uncoupled.vi" Type="VI" URL="../subVI/main_sim_log_uncoupled.vi"/>
				</Item>
				<Item Name="Plant" Type="Folder">
					<Item Name="plant_pause.vi" Type="VI" URL="../subVI/plant_pause.vi"/>
					<Item Name="plant_stop.vi" Type="VI" URL="../subVI/plant_stop.vi"/>
					<Item Name="plant_structure.vi" Type="VI" URL="../plant_structure.vi"/>
				</Item>
				<Item Name="Plot" Type="Folder">
					<Item Name="Tree Control" Type="Folder">
						<Item Name="plot_tree_clear.vi" Type="VI" URL="../subVI/plot_tree_clear.vi"/>
						<Item Name="plot_tree_control.vi" Type="VI" URL="../subVI/plot_tree_control.vi"/>
					</Item>
					<Item Name="plot_close.vi" Type="VI" URL="../subVI/plot_close.vi"/>
					<Item Name="plot_display.vi" Type="VI" URL="../subVI/plot_display.vi"/>
					<Item Name="plot_init_panel.vi" Type="VI" URL="../subVI/plot_init_panel.vi"/>
					<Item Name="plot_load_sim_output.vi" Type="VI" URL="../subVI/plot_load_sim_output.vi"/>
					<Item Name="plot_main.vi" Type="VI" URL="../subVI/plot_main.vi"/>
					<Item Name="plot_read_csv.vi" Type="VI" URL="../subVI/plot_read_csv.vi"/>
					<Item Name="plot_read_output_file.vi" Type="VI" URL="../subVI/plot_read_output_file.vi"/>
					<Item Name="plot_run.vi" Type="VI" URL="../subVI/plot_run.vi"/>
				</Item>
			</Item>
		</Item>
		<Item Name="initializer.vi" Type="VI" URL="../subVI/initializer.vi"/>
		<Item Name="main.vi" Type="VI" URL="../main.vi"/>
		<Item Name="Dependencies" Type="Dependencies">
			<Item Name="vi.lib" Type="Folder">
				<Item Name="Application Directory.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/file.llb/Application Directory.vi"/>
				<Item Name="BuildHelpPath.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/BuildHelpPath.vi"/>
				<Item Name="Check Special Tags.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Check Special Tags.vi"/>
				<Item Name="Check if File or Folder Exists.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/libraryn.llb/Check if File or Folder Exists.vi"/>
				<Item Name="Clear Errors.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Clear Errors.vi"/>
				<Item Name="Convert property node font to graphics font.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Convert property node font to graphics font.vi"/>
				<Item Name="Details Display Dialog.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Details Display Dialog.vi"/>
				<Item Name="DialogType.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/DialogType.ctl"/>
				<Item Name="DialogTypeEnum.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/DialogTypeEnum.ctl"/>
				<Item Name="Draw Flattened Pixmap.vi" Type="VI" URL="/&lt;vilib&gt;/picture/picture.llb/Draw Flattened Pixmap.vi"/>
				<Item Name="ErrWarn.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/ErrWarn.ctl"/>
				<Item Name="Error Cluster From Error Code.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Error Cluster From Error Code.vi"/>
				<Item Name="Error Code Database.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Error Code Database.vi"/>
				<Item Name="Escape Characters for HTTP.vi" Type="VI" URL="/&lt;vilib&gt;/printing/PathToURL.llb/Escape Characters for HTTP.vi"/>
				<Item Name="Find Tag.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Find Tag.vi"/>
				<Item Name="FixBadRect.vi" Type="VI" URL="/&lt;vilib&gt;/picture/pictutil.llb/FixBadRect.vi"/>
				<Item Name="Format Message String.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Format Message String.vi"/>
				<Item Name="FormatTime String.vi" Type="VI" URL="/&lt;vilib&gt;/express/express execution control/ElapsedTimeBlock.llb/FormatTime String.vi"/>
				<Item Name="General Error Handler Core CORE.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/General Error Handler Core CORE.vi"/>
				<Item Name="General Error Handler.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/General Error Handler.vi"/>
				<Item Name="Get File Extension.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/libraryn.llb/Get File Extension.vi"/>
				<Item Name="Get String Text Bounds.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Get String Text Bounds.vi"/>
				<Item Name="Get Text Rect.vi" Type="VI" URL="/&lt;vilib&gt;/picture/picture.llb/Get Text Rect.vi"/>
				<Item Name="GetHelpDir.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/GetHelpDir.vi"/>
				<Item Name="GetRTHostConnectedProp.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/GetRTHostConnectedProp.vi"/>
				<Item Name="Is Path and Not Empty.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/file.llb/Is Path and Not Empty.vi"/>
				<Item Name="LVBoundsTypeDef.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/miscctls.llb/LVBoundsTypeDef.ctl"/>
				<Item Name="LVForegroundBackgroundColorsTypeDef.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/miscctls.llb/LVForegroundBackgroundColorsTypeDef.ctl"/>
				<Item Name="LVPositionTypeDef.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/miscctls.llb/LVPositionTypeDef.ctl"/>
				<Item Name="LVRectTypeDef.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/miscctls.llb/LVRectTypeDef.ctl"/>
				<Item Name="Longest Line Length in Pixels.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Longest Line Length in Pixels.vi"/>
				<Item Name="NI_FileType.lvlib" Type="Library" URL="/&lt;vilib&gt;/Utility/lvfile.llb/NI_FileType.lvlib"/>
				<Item Name="NI_Matrix.lvlib" Type="Library" URL="/&lt;vilib&gt;/Analysis/Matrix/NI_Matrix.lvlib"/>
				<Item Name="NI_PackedLibraryUtility.lvlib" Type="Library" URL="/&lt;vilib&gt;/Utility/LVLibp/NI_PackedLibraryUtility.lvlib"/>
				<Item Name="Not Found Dialog.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Not Found Dialog.vi"/>
				<Item Name="Open URL in Default Browser (path).vi" Type="VI" URL="/&lt;vilib&gt;/Platform/browser.llb/Open URL in Default Browser (path).vi"/>
				<Item Name="Open URL in Default Browser (string).vi" Type="VI" URL="/&lt;vilib&gt;/Platform/browser.llb/Open URL in Default Browser (string).vi"/>
				<Item Name="Open URL in Default Browser core.vi" Type="VI" URL="/&lt;vilib&gt;/Platform/browser.llb/Open URL in Default Browser core.vi"/>
				<Item Name="Open URL in Default Browser.vi" Type="VI" URL="/&lt;vilib&gt;/Platform/browser.llb/Open URL in Default Browser.vi"/>
				<Item Name="Path to URL inner.vi" Type="VI" URL="/&lt;vilib&gt;/printing/PathToURL.llb/Path to URL inner.vi"/>
				<Item Name="Path to URL.vi" Type="VI" URL="/&lt;vilib&gt;/printing/PathToURL.llb/Path to URL.vi"/>
				<Item Name="Search and Replace Pattern.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Search and Replace Pattern.vi"/>
				<Item Name="Set Bold Text.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Set Bold Text.vi"/>
				<Item Name="Set Cursor (Cursor ID).vi" Type="VI" URL="/&lt;vilib&gt;/Utility/cursorutil.llb/Set Cursor (Cursor ID).vi"/>
				<Item Name="Set Cursor (Icon Pict).vi" Type="VI" URL="/&lt;vilib&gt;/Utility/cursorutil.llb/Set Cursor (Icon Pict).vi"/>
				<Item Name="Set Cursor.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/cursorutil.llb/Set Cursor.vi"/>
				<Item Name="Set String Value.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Set String Value.vi"/>
				<Item Name="Space Constant.vi" Type="VI" URL="/&lt;vilib&gt;/dlg_ctls.llb/Space Constant.vi"/>
				<Item Name="System Exec.vi" Type="VI" URL="/&lt;vilib&gt;/Platform/system.llb/System Exec.vi"/>
				<Item Name="TagReturnType.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/TagReturnType.ctl"/>
				<Item Name="Three Button Dialog CORE.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Three Button Dialog CORE.vi"/>
				<Item Name="Three Button Dialog.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Three Button Dialog.vi"/>
				<Item Name="Trim Whitespace.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Trim Whitespace.vi"/>
				<Item Name="eventvkey.ctl" Type="VI" URL="/&lt;vilib&gt;/event_ctls.llb/eventvkey.ctl"/>
				<Item Name="ex_CorrectErrorChain.vi" Type="VI" URL="/&lt;vilib&gt;/express/express shared/ex_CorrectErrorChain.vi"/>
				<Item Name="imagedata.ctl" Type="VI" URL="/&lt;vilib&gt;/picture/picture.llb/imagedata.ctl"/>
				<Item Name="subDisplayMessage.vi" Type="VI" URL="/&lt;vilib&gt;/express/express output/DisplayMessageBlock.llb/subDisplayMessage.vi"/>
				<Item Name="subElapsedTime.vi" Type="VI" URL="/&lt;vilib&gt;/express/express execution control/ElapsedTimeBlock.llb/subElapsedTime.vi"/>
				<Item Name="subFile Dialog.vi" Type="VI" URL="/&lt;vilib&gt;/express/express input/FileDialogBlock.llb/subFile Dialog.vi"/>
				<Item Name="whitespace.ctl" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/whitespace.ctl"/>
			</Item>
		</Item>
		<Item Name="Build Specifications" Type="Build"/>
	</Item>
</Project>
